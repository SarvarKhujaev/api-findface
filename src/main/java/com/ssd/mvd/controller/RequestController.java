package com.ssd.mvd.controller;

import java.util.List;

import com.ssd.mvd.inspectors.Config;
import com.ssd.mvd.inspectors.LogInspector;
import com.ssd.mvd.inspectors.SerDes;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.netty.handler.timeout.ReadTimeoutException;

import com.ssd.mvd.entity.*;
import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.entity.modelForGai.Tonirovka;
import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
import com.ssd.mvd.entity.modelForGai.ViolationsList;
import com.ssd.mvd.entity.boardCrossing.CrossBoardInfo;
import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.modelForFioOfPerson.PersonTotalDataByFIO;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.handler.annotation.MessageMapping;

@RestController
public final class RequestController extends LogInspector {
    private static String token;

    @MessageMapping ( value = "PING" )
    public Mono< Boolean > ping () {
        return super.convert( Boolean.TRUE );
    }

    @MessageMapping ( value = "GET_CAR_TONIROVKA" )
    public Mono< Tonirovka > getCarTonirovka (
            final ApiResponseModel apiResponseModel
    ) {
        super.logging( "Request for: " + Methods.GET_TONIROVKA + " : " + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetVehicleTonirovka()
                .apply( apiResponseModel.getStatus().getMessage() )
                .onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert(
                                new Tonirovka().generate(
                                        throwable.getMessage(),
                                        Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                )
                        )
                ).onErrorReturn(
                        new Tonirovka().generate(
                                Errors.SERVICE_WORK_ERROR.name(),
                                Errors.EXTERNAL_SERVICE_500_ERROR
                        )
                )
                : super.convert(
                        new Tonirovka().generate(
                                super.getErrorResponse.get()
                        )
                );
    }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_BY_FIO" ) // возвращает данные по ФИО человека
    public Mono< PersonTotalDataByFIO > getPersonTotalDataByFIO ( final FIO fio ) {
        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetPersonTotalDataByFIO()
                .apply( fio )
                .onErrorContinue( ( error, object ) -> super.logging(
                            error,
                            Methods.GET_PERSON_TOTAL_DATA_BY_FIO,
                            fio.toString()
                    )
                ).onErrorReturn(
                        PersonTotalDataByFIO
                                .generate()
                                .generate(
                                        Errors.SERVICE_WORK_ERROR.name(),
                                        Errors.SERVICE_WORK_ERROR
                                )
                )
                : super.convert(
                        PersonTotalDataByFIO
                                .generate()
                                .generate( super.getErrorResponse.get() )
                );
    }

    @MessageMapping ( value = "GET_CAR_TOTAL_DATA" ) // возвращает данные по номеру машины
    public Mono< CarTotalData > getCarTotalData (
            final ApiResponseModel apiResponseModel
    ) {
        super.logging( "Gos number: " + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? Mono.zip(
                        SerDes
                                .getSerDes()
                                .getGetVehicleTonirovka()
                                .apply( apiResponseModel.getStatus().getMessage() ),
                        SerDes
                                .getSerDes()
                                .getGetVehicleData()
                                .apply( apiResponseModel.getStatus().getMessage() ),
                        SerDes
                                .getSerDes()
                                .getGetDoverennostList()
                                .apply( apiResponseModel.getStatus().getMessage() ),
                        SerDes
                                .getSerDes()
                                .getInsurance()
                                .apply( apiResponseModel.getStatus().getMessage() ),
                        SerDes
                                .getSerDes()
                                .getGetViolationList()
                                .apply( apiResponseModel.getStatus().getMessage() )
                ).map( CarTotalData::generate )
                .flatMap( carTotalData -> super.check( carTotalData )
                        ? SerDes
                        .getSerDes()
                        .getGetPsychologyCardByPinfl()
                        .apply( ApiResponseModel
                                .builder()
                                .status( Status
                                        .builder()
                                        .message( carTotalData.getModelForCar().getPinpp() )
                                        .build() )
                                .user( apiResponseModel.getUser() )
                                .build() )
                        .map( carTotalData::save )
                        .onErrorResume(
                                io.netty.handler.timeout.ReadTimeoutException.class,
                                throwable -> super.convert(
                                        new CarTotalData().generate(
                                                throwable.getMessage(),
                                                Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                        )
                                )
                        )
                        : super.convert( carTotalData ) )
                .onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert(
                                new CarTotalData().generate(
                                        throwable.getMessage(),
                                        Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                )
                        )
                ).onErrorReturn(
                        new CarTotalData().generate(
                                Errors.SERVICE_WORK_ERROR.name(),
                                Errors.EXTERNAL_SERVICE_500_ERROR
                        )
                )
                : super.convert( new CarTotalData().generate( super.getErrorResponse.get() ) );
    }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA" ) // возвращает данные по фотографии
    public Mono< PsychologyCard > getPersonTotalData (
            final ApiResponseModel apiResponseModel
    ) {
        String base64url = apiResponseModel.getStatus().getMessage();
        token = base64url.split( "@" )[ 1 ];
        base64url = base64url.split( "@" )[ 0 ];

        return super.checkParam( base64url )
                ? FindFaceComponent
                .getInstance()
                .getPapilonList
                .apply( base64url )
                .filter( results -> super.isCollectionNotEmpty( results.getResults() ) )
                .flatMap( results -> Config.flag
                        ? results
                        .getResults()
                        .get( 0 )
                        .getCountry()
                        .equals( "УЗБЕКИСТАН" )
                        ? SerDes
                        .getSerDes()
                        .getGetPsychologyCardByImage()
                        .apply( results, apiResponseModel )
                        .onErrorResume(
                                io.netty.handler.timeout.ReadTimeoutException.class,
                                throwable -> super.convert(
                                        new PsychologyCard().generate(
                                                throwable.getMessage(),
                                                Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                        )
                                )
                        )
                        : SerDes
                        .getSerDes()
                        .getPsychologyCard(
                                token,
                                PsychologyCard.generate( results ),
                                apiResponseModel
                        )
                        : super.convert( new PsychologyCard().generate( super.getErrorResponse.get() ) )
                )
                : super.convert(
                        new PsychologyCard().generate(
                                Errors.WRONG_PARAMS.name(),
                                Errors.SERVICE_WORK_ERROR
                        )
                );
    }

    @MessageMapping ( value = "GET_PERSONAL_CADASTOR" ) // возвращает данные по номеру кадастра
    public Flux< PsychologyCard > getPersonalCadastor (
            final ApiResponseModel apiResponseModel
    ) {
        super.logging( "Cadaster value: " + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetCadaster()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMapMany( data -> super.isCollectionNotEmpty( data.getPermanentRegistration() )
                        ? Flux.fromStream( data.getPermanentRegistration().stream() )
                        .flatMap( person -> SerDes
                                .getSerDes()
                                .getGetModelForPassport()
                                .apply( person.getPPsp(), person.getPDateBirth() )
                                .flatMap( data1 -> SerDes
                                        .getSerDes()
                                        .getGetPsychologyCardByData()
                                        .apply( data1, apiResponseModel )
                                        .onErrorResume(
                                                io.netty.handler.timeout.ReadTimeoutException.class,
                                                throwable -> super.convert(
                                                        new PsychologyCard().generate(
                                                                throwable.getMessage(),
                                                                Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                                        )
                                                )
                                        )
                                )
                        ).onErrorResume(
                                io.netty.handler.timeout.ReadTimeoutException.class,
                                throwable -> super.convert(
                                        new PsychologyCard().generate(
                                                throwable.getMessage(),
                                                Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                        )
                                )
                        ).onErrorReturn(
                                new PsychologyCard().generate(
                                        Errors.SERVICE_WORK_ERROR.name(),
                                        Errors.EXTERNAL_SERVICE_500_ERROR
                                )
                        )
                        : Flux.just(
                                new PsychologyCard().generate(
                                        apiResponseModel.getStatus().getMessage(),
                                        Errors.DATA_NOT_FOUND
                                )
                        )
                )
                : Flux.just( new PsychologyCard().generate( super.getErrorResponse.get() ) );
    }

    // возвращает данные по номеру машины в слуцчае если у человека роль IMITATION
    @MessageMapping ( value = "GET_CAR_TOTAL_DATA_BY_PINFL" )
    public Mono< CarTotalData > getCarTotalDataByPinfl (
            final ApiResponseModel apiResponseModel
    ) {
        super.logging( "PINFL: " + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetModelForCarList()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMap( modelForCarList -> super.objectIsNotNull( modelForCarList )
                        && super.isCollectionNotEmpty( modelForCarList.getModelForCarList() )
                                ? this.getCarTotalData(
                                        ApiResponseModel
                                            .builder()
                                            .status(
                                                    Status
                                                        .builder()
                                                        .message(
                                                                modelForCarList
                                                                    .getModelForCarList()
                                                                    .get( 0 )
                                                                    .getPlateNumber()
                                                        ).build()
                                            ).build()
                                    )
                                    : this.getCarTotalData(
                                            ApiResponseModel
                                                .builder()
                                                .status(
                                                        Status
                                                            .builder()
                                                            .message( "01Y456MA" )
                                                            .build()
                                                ).build()
                                    )
                ).onErrorResume(
                        ReadTimeoutException.class,
                        throwable -> super.convert(
                                new CarTotalData().generate(
                                        throwable.getMessage(),
                                        Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                )
                        )
                ).onErrorReturn(
                        new CarTotalData().generate(
                                Errors.SERVICE_WORK_ERROR.name(),
                                Errors.EXTERNAL_SERVICE_500_ERROR
                        )
                )
                : super.convert( new CarTotalData().generate( super.getErrorResponse.get() ) );
    }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_BY_PINFL" ) // возвращает данные по Пинфл
    public Mono< PsychologyCard > getPersonTotalDataByPinfl (
            final ApiResponseModel apiResponseModel
    ) {
        super.logging( "PINFL: " + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? super.checkParam( apiResponseModel.getStatus().getMessage() )
                ? SerDes
                .getSerDes()
                .getGetPsychologyCardByPinfl()
                .apply( apiResponseModel )
                .onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert(
                                new PsychologyCard().generate(
                                        throwable.getMessage(),
                                        Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                )
                        )
                )
                : super.convert(
                        new PsychologyCard().generate(
                                Errors.WRONG_PARAMS.name(),
                                Errors.SERVICE_WORK_ERROR
                        )
                )
                : super.convert( new PsychologyCard().generate( super.getErrorResponse.get() ) );
    }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_BY_PASSPORT_AND_BIRTHDATE" ) // возвращает данные по номеру паспорта
    public Mono< PsychologyCard > getPersonDataByPassportSeriesAndBirthdate (
            final ApiResponseModel apiResponseModel
    ) {
        if ( !super.checkParam( apiResponseModel.getStatus().getMessage() ) ) {
            return super.convert(
                    new PsychologyCard().generate(
                            Errors.WRONG_PARAMS.name(),
                            Errors.SERVICE_WORK_ERROR
                    )
            );
        }

        final String[] strings = apiResponseModel.getStatus().getMessage().split( "_" );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetModelForPassport()
                .apply( strings[ 0 ], strings[ 1 ] )
                .flatMap( data -> SerDes
                        .getSerDes()
                        .getGetPsychologyCardByData()
                        .apply( data, apiResponseModel )
                ).onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert(
                                new PsychologyCard().generate(
                                        throwable.getMessage(),
                                        Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                )
                        )
                ).onErrorReturn(
                        new PsychologyCard().generate(
                                Errors.SERVICE_WORK_ERROR.name(),
                                Errors.SERVICE_WORK_ERROR
                        )
                )
                : super.convert( new PsychologyCard().generate( super.getErrorResponse.get() ) );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // данные по авто

    @MessageMapping ( value = "GET_CAR_DATA_BY_GOS_NUMBER_INITIAL" ) // используется при запросе по номеру машины
    public Mono< CarTotalData > GET_CAR_DATA_BY_GOS_NUMBER_INITIAL (
            final ApiResponseModel apiResponseModel
    ) {
        super.logging( "Gos number: " + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetVehicleData()
                .apply( apiResponseModel.getStatus().getMessage() )
                .map( CarTotalData::generate )
                .flatMap( carTotalData -> SerDes
                        .getSerDes()
                        .getGetPsychologyCardByPinflInitial()
                        .apply( apiResponseModel.changeMessage( carTotalData.getModelForCar().getPinpp() ) )
                        .map( carTotalData::save )
                ).onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert(
                                new CarTotalData().generate(
                                        throwable.getMessage(),
                                        Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                )
                        )
                ).onErrorReturn(
                        new CarTotalData().generate(
                                Errors.SERVICE_WORK_ERROR.name(),
                                Errors.EXTERNAL_SERVICE_500_ERROR
                        )
                )
                : super.convert( new CarTotalData().generate( super.getErrorResponse.get() ) );
    }

    @MessageMapping ( value = "GET_MODEL_FOR_CAR_LIST_INITIAL" ) // используется при запросе по пинфл человека
    public Mono< ModelForCarList > GET_MODEL_FOR_CAR_LIST_INITIAL (
            final ApiResponseModel apiResponseModel
    ) {
        super.logging( "PINFL in GET_CAR_DATA_BY_PINFL_INITIAL: " + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetModelForCarList()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMap( modelForCarList -> super.objectIsNotNull( modelForCarList )
                        && super.isCollectionNotEmpty( modelForCarList.getModelForCarList() )
                        ? SerDes
                        .getSerDes()
                        .getFindAllAboutCarList()
                        .apply( modelForCarList )
                        : super.convert( modelForCarList) )
                .onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert(
                                new ModelForCarList().generate(
                                        throwable.getMessage(),
                                        Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                )
                        )
                ).onErrorReturn(
                        new ModelForCarList().generate(
                                Errors.SERVICE_WORK_ERROR.name(),
                                Errors.EXTERNAL_SERVICE_500_ERROR
                        )
                )
                : super.convert( new ModelForCarList().generate( super.getErrorResponse.get() ) );
    }

    @MessageMapping ( value = "GET_PERSON_FINES_FOR_DRIVING" ) // возвращает все штрафы от гаи по номеру машины
    public Mono< ViolationsList > GET_PERSON_FINES_FOR_DRIVING (
            final ApiResponseModel apiResponseModel
    ) {
        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetViolationList()
                .apply( apiResponseModel.getStatus().getMessage() )
                .onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert(
                                new ViolationsList().generate(
                                        throwable.getMessage(),
                                        Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                )
                        )
                )
                .onErrorReturn(
                        new ViolationsList().generate(
                                Errors.SERVICE_WORK_ERROR.name(),
                                Errors.EXTERNAL_SERVICE_500_ERROR
                        )
                )
                : super.convert( new ViolationsList().generate( super.getErrorResponse.get() ) );
    }

    // ---------------------------------------------------------------- дааные для человека

    @MessageMapping ( value = "GET_PINPP" )
    public Mono< Pinpp > getPINPP (
            final ApiResponseModel apiResponseModel
    ) {
        super.logging( "Request for: " + Methods.GET_PINPP + " : " + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetPinpp()
                .apply( apiResponseModel.getStatus().getMessage() )
                .onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert(
                                new Pinpp().generate(
                                        throwable.getMessage(),
                                        Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                )
                        )
                )
                .onErrorReturn(
                        new Pinpp().generate(
                                Errors.SERVICE_WORK_ERROR.name(),
                                Errors.EXTERNAL_SERVICE_500_ERROR
                        )
                )
                : super.convert( new Pinpp().generate( super.getErrorResponse.get() ) );
    }

    @MessageMapping ( value = "GET_VIOLATION_LIST_BY_PINFL" ) // возвращает список правонарушений гражданина
    public Mono< List > GET_VIOLATION_LIST_BY_PINFL (
            final ApiResponseModel apiResponseModel
    ) {
        return Config.flag
                ? super.checkParam( apiResponseModel.getStatus().getMessage() )
                ? FindFaceComponent
                    .getInstance()
                    .getViolationListByPinfl
                    .apply( apiResponseModel.getStatus().getMessage() )
                    .onErrorResume(
                            io.netty.handler.timeout.ReadTimeoutException.class,
                            throwable -> super.convert( super.emptyList() )
                    ).onErrorReturn( super.emptyList() )
                    : super.convert( super.emptyList() )
                : super.convert( super.emptyList() );
    }

    @MessageMapping ( value = "GET_CROSS_BOARDING" )
    public Mono< CrossBoardInfo > GET_PERSON_BOARD_CROSSING (
            final ApiResponseModel apiResponseModel
    ) {
        super.logging( "Request for: " + Methods.GET_CROSS_BOARDING + " : " + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetCrossBoardInfo()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMap( crossBoardInfo -> super.objectIsNotNull( crossBoardInfo.getData() )
                        && super.isCollectionNotEmpty( crossBoardInfo.getData() )
                        && super.isCollectionNotEmpty( crossBoardInfo.getData().get( 0 ).getCrossBoardList() )
                        ? SerDes
                        .getSerDes()
                        .getAnalyzeCrossData()
                        .apply( crossBoardInfo )
                        : super.convert( crossBoardInfo )
                ).onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert(
                                new CrossBoardInfo().generate(
                                        throwable.getMessage(),
                                        Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                )
                        )
                ).onErrorReturn(
                        new CrossBoardInfo().generate(
                                Errors.SERVICE_WORK_ERROR.name(),
                                Errors.EXTERNAL_SERVICE_500_ERROR
                        )
                )
                : super.convert( new CrossBoardInfo().generate( super.getErrorResponse.get() ) );
    }

    @MessageMapping ( value = "GET_TEMPORARY_OR_PERMANENT_REGISTRATION" ) // возвращает временную или постоянную прописку человека
    public Mono< ModelForAddress > GET_TEMPORARY_REGISTRATION (
            final ApiResponseModel apiResponseModel
    ) {
        super.logging( "pCitizen value in GET_TEMPORARY_OR_PERMANENT_REGISTRATION: " + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetModelForAddress()
                .apply( apiResponseModel.getStatus().getMessage() )
                .onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert(
                                new ModelForAddress().generate(
                                        throwable.getMessage(),
                                        Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                )
                        )
                ).onErrorReturn(
                        new ModelForAddress().generate(
                                Errors.SERVICE_WORK_ERROR.name(),
                                Errors.EXTERNAL_SERVICE_500_ERROR
                        )
                )
                : super.convert( new ModelForAddress().generate( super.getErrorResponse.get() ) );
    }

    @MessageMapping ( value = "GET_PERSONAL_CADASTOR_INITIAL" ) // возвращает данные по номеру кадастра
    public Flux< PsychologyCard > GET_PERSONAL_CADASTOR_INITIAL (
            final ApiResponseModel apiResponseModel
    ) {
        super.logging( "Cadaster value in GET_PERSONALINITIAL_CADASTOR: " + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetCadaster()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMapMany( data -> super.isCollectionNotEmpty( data.getPermanentRegistration() )
                        ? Flux.fromStream(
                                data
                                        .getPermanentRegistration()
                                        .stream()
                        ).flatMap( person -> SerDes
                                .getSerDes()
                                .getGetModelForPassport()
                                .apply( person.getPPsp(), person.getPDateBirth() )
                                .flatMap( data1 -> super.check( data1 )
                                        ? SerDes
                                        .getSerDes()
                                        .getGetPsychologyCardByPinflInitial()
                                        .apply( apiResponseModel.changeMessage( data1.getData().getPerson().getPinpp() ) )
                                        .map( psychologyCard -> psychologyCard.save( data1 ) )
                                        .onErrorResume(
                                                ReadTimeoutException.class,
                                                throwable -> super.convert(
                                                        new PsychologyCard().generate(
                                                                throwable.getMessage(),
                                                                Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                                        )
                                                )
                                        )
                                        : super.convert(
                                                new PsychologyCard().generate(
                                                        person.getPPsp() + " : " + person.getPDateBirth(),
                                                        Errors.DATA_NOT_FOUND
                                                )
                                        )
                                )
                        ).onErrorResume(
                                ReadTimeoutException.class,
                                throwable -> super.convert(
                                        new PsychologyCard().generate(
                                                throwable.getMessage(),
                                                Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                        )
                                )
                        ).onErrorReturn(
                                new PsychologyCard().generate(
                                        Errors.SERVICE_WORK_ERROR.name(),
                                        Errors.EXTERNAL_SERVICE_500_ERROR
                                )
                        )
                        : Flux.just(
                                new PsychologyCard().generate(
                                        apiResponseModel.getStatus().getMessage(),
                                        Errors.DATA_NOT_FOUND
                                )
                        )
                )
                : Flux.just( new PsychologyCard().generate( super.getErrorResponse.get() ) );
    }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_INITIAL" ) // возвращает данные по фотографии
    public Mono< PsychologyCard > GET_PERSON_INITIAL_TOTAL_DATA (
            final ApiResponseModel apiResponseModel
    ) {
        String base64url = apiResponseModel.getStatus().getMessage();
        token = base64url.split( "@" )[ 1 ];
        base64url = base64url.split( "@" )[ 0 ];

        return super.checkParam( base64url )
                ? FindFaceComponent
                .getInstance()
                .getPapilonList
                .apply( base64url )
                .filter( results -> super.isCollectionNotEmpty( results.getResults() ) )
                .flatMap( results -> Config.flag
                        ? results
                        .getResults()
                        .get( 0 )
                        .getCountry()
                        .equals( "УЗБЕКИСТАН" )
                        ? super.convert( PsychologyCard.generate( results ) )
                        .map( psychologyCard -> psychologyCard.save( results ) )
                        : SerDes
                        .getSerDes()
                        .getPsychologyCard( token, PsychologyCard.generate( results ), apiResponseModel )
                        : super.convert( new PsychologyCard().generate( super.getErrorResponse.get() ) ) )
                .onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert(
                                new PsychologyCard().generate(
                                        throwable.getMessage(),
                                        Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                )
                        )
                ).onErrorReturn(
                        new PsychologyCard().generate(
                                Errors.SERVICE_WORK_ERROR.name(),
                                Errors.EXTERNAL_SERVICE_500_ERROR
                        )
                )
                : super.convert(
                        new PsychologyCard().generate(
                                Errors.WRONG_PARAMS.name(),
                                Errors.SERVICE_WORK_ERROR
                        )
                );
    }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_BY_PINFL_INITIAL" ) // возвращает данные по Пинфл
    public Mono< PsychologyCard > GET_PERSON_TOTAL_DATA_BY_PINFL_INITIAL (
            final ApiResponseModel apiResponseModel
    ) {
        super.logging( "PINFL in GET_PERSON_TOTAL_DATA_BY_PINFL_INITIAL: " + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? super.checkParam( apiResponseModel.getStatus().getMessage() )
                ? SerDes
                .getSerDes()
                .getGetPsychologyCardByPinflInitial()
                .apply( apiResponseModel )
                .onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert(
                                new PsychologyCard().generate(
                                        throwable.getMessage(),
                                        Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                )
                        )
                )
                : super.convert(
                        new PsychologyCard().generate(
                                Errors.WRONG_PARAMS.name(),
                                Errors.SERVICE_WORK_ERROR
                        )
                )
                : super.convert( new PsychologyCard().generate( super.getErrorResponse.get() ) );
    }

    @MessageMapping ( value = "GET_PERSON_DATA_BY_PASSPORT_AND_BIRTHDATE_INITIAL" ) // возвращает данные по номеру паспорта
    public Mono< PsychologyCard > GET_PERSON_INITIAL_DATA_BY_PASSPORT_AND_BIRTHDATE (
            final ApiResponseModel apiResponseModel
    ) {
        if ( !super.checkParam( apiResponseModel.getStatus().getMessage() ) ) {
            return super.convert(
                    new PsychologyCard().generate(
                            Errors.WRONG_PARAMS.name(),
                            Errors.SERVICE_WORK_ERROR
                    )
            );
        }

        final String[] strings = apiResponseModel.getStatus().getMessage().split( "_" );

        super.logging( "Passport: " + strings[0] + " : " + strings[1] );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetModelForPassport()
                .apply( strings[ 0 ], strings[ 1 ] )
                .flatMap( data -> SerDes
                        .getSerDes()
                        .getGetPsychologyCardByPinflInitial()
                        .apply( apiResponseModel.changeMessage( data.getData().getPerson().getPinpp() ) )
                        .map( psychologyCard -> psychologyCard.save( data ) )
                ).onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert(
                                new PsychologyCard().generate(
                                        throwable.getMessage(),
                                        Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED )
                                )
                ).onErrorReturn(
                        new PsychologyCard().generate(
                                Errors.SERVICE_WORK_ERROR.name(),
                                Errors.SERVICE_WORK_ERROR
                        )
                )
                : super.convert( new PsychologyCard().generate( super.getErrorResponse.get() ) );
    }
}