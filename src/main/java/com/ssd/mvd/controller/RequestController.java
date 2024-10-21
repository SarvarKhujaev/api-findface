package com.ssd.mvd.controller;

import com.ssd.mvd.inspectors.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.lang.ref.WeakReference;
import io.netty.handler.timeout.ReadTimeoutException;

import com.ssd.mvd.entity.*;
import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.entity.modelForGai.Tonirovka;
import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
import com.ssd.mvd.entity.response.ApiResponseModel;
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
            @lombok.NonNull final ApiResponseModel apiResponseModel
    ) {
        super.logging( "Request for: " + Methods.GET_TONIROVKA + SPACE_WITH_DOUBLE_DOTS + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetVehicleTonirovka()
                .apply( apiResponseModel.getStatus().getMessage() )
                .onErrorResume(
                        throwable -> super.completeError(
                                EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                EntitiesInstances.TONIROVKA.get()
                        )
                ).onErrorReturn( super.completeError( EntitiesInstances.TONIROVKA.get() ) )
                : super.convert( EntitiesInstances.TONIROVKA.get().generate( super.getErrorResponse() ) );
    }

    @SuppressWarnings(
            value = "возвращает данные по ФИО человека"
    )
    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_BY_FIO" )
    public Mono< PersonTotalDataByFIO > getPersonTotalDataByFIO ( @lombok.NonNull final FIO fio ) {
        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetPersonTotalDataByFIO()
                .apply( fio )
                .onErrorContinue(
                        ( error, object ) -> super.logging(
                                error,
                                EntitiesInstances.PERSON_TOTAL_DATA_BY_FIO.get(),
                                fio.toString()
                        )
                ).onErrorReturn( super.completeError( EntitiesInstances.PERSON_TOTAL_DATA_BY_FIO.get() ) )
                : super.convert( EntitiesInstances.PERSON_TOTAL_DATA_BY_FIO.get().generate( super.getErrorResponse() ) );
    }

    @SuppressWarnings( value = "возвращает данные по номеру машины" )
    @MessageMapping ( value = "GET_CAR_TOTAL_DATA" )
    public Mono< CarTotalData > getCarTotalData (
            @lombok.NonNull final ApiResponseModel apiResponseModel
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
                        .apply(
                                EntitiesInstances.generateResponse(
                                        carTotalData.getModelForCar().getPinpp(),
                                        apiResponseModel.getUser()
                                )
                        ).map( carTotalData::save )
                        .onErrorResume(
                                throwable -> super.completeError(
                                        EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                        EntitiesInstances.CAR_TOTAL_DATA.get()
                                )
                        )
                        : super.convert( carTotalData )
                ).onErrorReturn( super.completeError( EntitiesInstances.CAR_TOTAL_DATA.get() ) )
                : super.convert( EntitiesInstances.CAR_TOTAL_DATA.get().generate( super.getErrorResponse() ) );
    }

    @SuppressWarnings( value = "возвращает данные по фотографии" )
    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA" )
    public Mono< PsychologyCard > getPersonTotalData (
            @lombok.NonNull final ApiResponseModel apiResponseModel
    ) {
        String base64url = apiResponseModel.getStatus().getMessage();
        token = base64url.split( "@" )[ 1 ];
        base64url = base64url.split( "@" )[ 0 ];

        return super.checkParam( base64url )
                ? FindFaceComponent
                .getInstance()
                .getPapilonList
                .apply( base64url )
                .filter( results -> isCollectionNotEmpty( results.getResults() ) )
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
                                throwable -> super.completeError(
                                        EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                        EntitiesInstances.PSYCHOLOGY_CARD.get()
                                )
                        )
                        : SerDes
                        .getSerDes()
                        .getPsychologyCard(
                                token,
                                PsychologyCard.generate( results ),
                                apiResponseModel
                        )
                        : super.convert( EntitiesInstances.PSYCHOLOGY_CARD.get().generate( super.getErrorResponse() ) )
                )
                : super.convert( super.completeError( EntitiesInstances.PSYCHOLOGY_CARD.get() ) );
    }

    @SuppressWarnings( value = "возвращает данные по номеру кадастра" )
    @MessageMapping ( value = "GET_PERSONAL_CADASTOR" )
    public Flux< PsychologyCard > getPersonalCadastor (
            @lombok.NonNull final ApiResponseModel apiResponseModel
    ) {
        super.logging( "Cadaster value: " + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetCadaster()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMapMany( data -> isCollectionNotEmpty( data.getPermanentRegistration() )
                        ? Flux.fromStream( data.getPermanentRegistration().stream() )
                        .flatMap( person -> SerDes
                                .getSerDes()
                                .getGetModelForPassport()
                                .apply( person.getPPsp() + person.getPDateBirth() )
                                .flatMap( data1 -> SerDes
                                        .getSerDes()
                                        .getGetPsychologyCardByData()
                                        .apply( data1, apiResponseModel )
                                        .onErrorResume(
                                                throwable -> super.completeError(
                                                        EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                                        EntitiesInstances.PSYCHOLOGY_CARD.get()
                                                )
                                        )
                                )
                        ).onErrorResume(
                                throwable -> super.completeError(
                                        EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                        EntitiesInstances.PSYCHOLOGY_CARD.get()
                                )
                        ).onErrorReturn( super.completeError( EntitiesInstances.PSYCHOLOGY_CARD.get() ) )
                        : Flux.just(
                                EntitiesInstances.PSYCHOLOGY_CARD.get().generate(
                                        apiResponseModel.getStatus().getMessage(),
                                        Errors.DATA_NOT_FOUND
                                )
                        )
                )
                : Flux.just( EntitiesInstances.PSYCHOLOGY_CARD.get().generate( super.getErrorResponse() ) );
    }

    @SuppressWarnings(
            value = "возвращает данные по номеру машины в слуцчае если у человека роль IMITATION"
    )
    @MessageMapping ( value = "GET_CAR_TOTAL_DATA_BY_PINFL" )
    public Mono< CarTotalData > getCarTotalDataByPinfl (
            @lombok.NonNull final ApiResponseModel apiResponseModel
    ) {
        super.logging( "PINFL: " + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetModelForCarList()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMap( modelForCarList -> objectIsNotNull( modelForCarList )
                        && isCollectionNotEmpty( modelForCarList.getModelForCarList() )
                                ? this.getCarTotalData(
                                        EntitiesInstances.generateResponse(
                                                modelForCarList
                                                        .getModelForCarList()
                                                        .get( 0 )
                                                        .getPlateNumber()
                                        )
                                )
                                : this.getCarTotalData( EntitiesInstances.generateResponse( "01Y456MA" ) )
                ).onErrorResume(
                        throwable -> super.completeError(
                                EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                EntitiesInstances.CAR_TOTAL_DATA.get()
                        )
                ).onErrorReturn( super.completeError( EntitiesInstances.CAR_TOTAL_DATA.get() ) )
                : super.convert( EntitiesInstances.CAR_TOTAL_DATA.get().generate( super.getErrorResponse() ) );
    }

    @SuppressWarnings( value = "возвращает данные по Пинфл" )
    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_BY_PINFL" )
    public Mono< PsychologyCard > getPersonTotalDataByPinfl (
            @lombok.NonNull final ApiResponseModel apiResponseModel
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
                        throwable -> super.completeError(
                                EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                EntitiesInstances.PSYCHOLOGY_CARD.get()
                        )
                )
                : super.convert( super.completeError( EntitiesInstances.PSYCHOLOGY_CARD.get() ) )
                : super.convert( EntitiesInstances.PSYCHOLOGY_CARD.get().generate( super.getErrorResponse() ) );
    }

    @SuppressWarnings( value = "возвращает данные по номеру паспорта" )
    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_BY_PASSPORT_AND_BIRTHDATE" )
    public Mono< PsychologyCard > getPersonDataByPassportSeriesAndBirthdate (
            @lombok.NonNull final ApiResponseModel apiResponseModel
    ) {
        if ( !super.checkParam( apiResponseModel.getStatus().getMessage() ) ) {
            return super.convert( EntitiesInstances.PSYCHOLOGY_CARD.get() );
        }

        final String[] strings = apiResponseModel.getStatus().getMessage().split( "_" );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetModelForPassport()
                .apply( strings[ 0 ] + strings[ 1 ] )
                .flatMap(
                        data -> SerDes
                                .getSerDes()
                                .getGetPsychologyCardByData()
                                .apply( data, apiResponseModel )
                ).onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.completeError(
                                EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                EntitiesInstances.PSYCHOLOGY_CARD.get()
                        )
                ).onErrorReturn( super.completeError( EntitiesInstances.PSYCHOLOGY_CARD.get() ) )
                : super.convert( EntitiesInstances.PSYCHOLOGY_CARD.get().generate( super.getErrorResponse() ) );
    }

    @SuppressWarnings( value = "используется при запросе по номеру машины" )
    @MessageMapping ( value = "GET_CAR_DATA_BY_GOS_NUMBER_INITIAL" )
    public Mono< CarTotalData > GET_CAR_DATA_BY_GOS_NUMBER_INITIAL (
            @lombok.NonNull final ApiResponseModel apiResponseModel
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
                        throwable -> super.completeError(
                                EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                EntitiesInstances.CAR_TOTAL_DATA.get()
                        )
                ).onErrorReturn( super.completeError( EntitiesInstances.CAR_TOTAL_DATA.get() ) )
                : super.convert( EntitiesInstances.CAR_TOTAL_DATA.get().generate( super.getErrorResponse() ) );
    }

    @SuppressWarnings( value = "используется при запросе по пинфл человека" )
    @MessageMapping ( value = "GET_MODEL_FOR_CAR_LIST_INITIAL" )
    public Mono< ModelForCarList > GET_MODEL_FOR_CAR_LIST_INITIAL (
            @lombok.NonNull final ApiResponseModel apiResponseModel
    ) {
        super.logging( "PINFL in GET_CAR_DATA_BY_PINFL_INITIAL: " + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetModelForCarList()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMap(
                        modelForCarList -> objectIsNotNull( modelForCarList )
                                && isCollectionNotEmpty( modelForCarList.getModelForCarList() )
                                ? SerDes
                                .getSerDes()
                                .getFindAllAboutCarList()
                                .apply( modelForCarList )
                                : super.convert( modelForCarList )
                ).onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.completeError(
                                EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                EntitiesInstances.MODEL_FOR_CAR_LIST.get()
                        )
                ).onErrorReturn( super.completeError( EntitiesInstances.MODEL_FOR_CAR_LIST.get() ) )
                : super.convert( EntitiesInstances.MODEL_FOR_CAR_LIST.get().generate( super.getErrorResponse() ) );
    }

    @SuppressWarnings( value = "возвращает все штрафы от гаи по номеру машины" )
    @MessageMapping ( value = "GET_PERSON_FINES_FOR_DRIVING" )
    public Mono< ViolationsList > GET_PERSON_FINES_FOR_DRIVING (
            @lombok.NonNull final ApiResponseModel apiResponseModel
    ) {
        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetViolationList()
                .apply( apiResponseModel.getStatus().getMessage() )
                .onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.completeError(
                                EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                EntitiesInstances.VIOLATIONS_LIST.get()
                        )
                ).onErrorReturn( super.completeError( EntitiesInstances.VIOLATIONS_LIST.get() ) )
                : super.convert( EntitiesInstances.VIOLATIONS_LIST.get().generate( super.getErrorResponse() ) );
    }

    // ---------------------------------------------------------------- дааные для человека

    @MessageMapping ( value = "GET_PINPP" )
    public Mono< Pinpp > getPINPP (
            @lombok.NonNull final ApiResponseModel apiResponseModel
    ) {
        super.logging( "Request for: " + Methods.GET_PINPP + SPACE_WITH_DOUBLE_DOTS + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetPinpp()
                .apply( apiResponseModel.getStatus().getMessage() )
                .onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.completeError(
                                EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                EntitiesInstances.PINPP.get()
                        )
                ).onErrorReturn( super.completeError( EntitiesInstances.PINPP.get() ) )
                : super.convert( EntitiesInstances.PINPP.get().generate( super.getErrorResponse() ) );
    }

    @SuppressWarnings( value = "возвращает список правонарушений гражданина" )
    @MessageMapping ( value = "GET_VIOLATION_LIST_BY_PINFL" )
    public Mono< List > GET_VIOLATION_LIST_BY_PINFL (
            @lombok.NonNull final ApiResponseModel apiResponseModel
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
            @lombok.NonNull final ApiResponseModel apiResponseModel
    ) {
        super.logging( "Request for: " + Methods.GET_CROSS_BOARDING + SPACE_WITH_DOUBLE_DOTS + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetCrossBoardInfo()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMap( crossBoardInfo -> objectIsNotNull( crossBoardInfo.getData() )
                        && isCollectionNotEmpty( crossBoardInfo.getData() )
                        && isCollectionNotEmpty( crossBoardInfo.getData().get( 0 ).getCrossBoardList() )
                        ? SerDes
                        .getSerDes()
                        .getAnalyzeCrossData()
                        .apply( crossBoardInfo )
                        : super.convert( crossBoardInfo )
                ).onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.completeError(
                                EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                EntitiesInstances.CROSS_BOARD_INFO.get()
                        )
                ).onErrorReturn( super.completeError( EntitiesInstances.CROSS_BOARD_INFO.get() ) )
                : super.convert( EntitiesInstances.CROSS_BOARD_INFO.get().generate( super.getErrorResponse() ) );
    }

    @SuppressWarnings( value = "возвращает временную или постоянную прописку человека" )
    @MessageMapping ( value = "GET_TEMPORARY_OR_PERMANENT_REGISTRATION" )
    public Mono< ModelForAddress > GET_TEMPORARY_REGISTRATION (
            @lombok.NonNull final ApiResponseModel apiResponseModel
    ) {
        super.logging( "pCitizen value in GET_TEMPORARY_OR_PERMANENT_REGISTRATION: " + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetModelForAddress()
                .apply( apiResponseModel.getStatus().getMessage() )
                .onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.completeError(
                                EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                EntitiesInstances.MODEL_FOR_ADDRESS.get()
                        )
                ).onErrorReturn( super.completeError( EntitiesInstances.MODEL_FOR_ADDRESS.get() ) )
                : super.convert( EntitiesInstances.MODEL_FOR_ADDRESS.get().generate( super.getErrorResponse() ) );
    }

    @SuppressWarnings( value = "возвращает данные по номеру кадастра" )
    @MessageMapping ( value = "GET_PERSONAL_CADASTOR_INITIAL" )
    public Flux< PsychologyCard > GET_PERSONAL_CADASTOR_INITIAL (
            @lombok.NonNull final ApiResponseModel apiResponseModel
    ) {
        super.logging( "Cadaster value in: : " + Methods.GET_PERSONAL_CADASTOR_INITIAL + apiResponseModel.getStatus().getMessage() );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetCadaster()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMapMany( data -> isCollectionNotEmpty( data.getPermanentRegistration() )
                        ? Flux.fromStream(
                                data.getPermanentRegistration().stream()
                        ).flatMap( person -> SerDes
                                .getSerDes()
                                .getGetModelForPassport()
                                .apply( person.getPPsp() + person.getPDateBirth() )
                                .flatMap( data1 -> super.check( data1 )
                                        ? SerDes
                                        .getSerDes()
                                        .getGetPsychologyCardByPinflInitial()
                                        .apply( apiResponseModel.changeMessage( data1.getData().getPerson().getPinpp() ) )
                                        .map( psychologyCard -> psychologyCard.save( data1 ) )
                                        .onErrorResume(
                                                ReadTimeoutException.class,
                                                throwable -> super.completeError(
                                                        EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                                        EntitiesInstances.PSYCHOLOGY_CARD.get()
                                                )
                                        )
                                        : super.convert( super.completeError( EntitiesInstances.PSYCHOLOGY_CARD.get() ) )
                                )
                        ).onErrorResume(
                                ReadTimeoutException.class,
                                throwable -> super.completeError(
                                        EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                        EntitiesInstances.PSYCHOLOGY_CARD.get()
                                )
                        ).onErrorReturn( super.completeError( EntitiesInstances.PSYCHOLOGY_CARD.get() ) )
                        : Flux.just(
                                EntitiesInstances.PSYCHOLOGY_CARD.get().generate(
                                        apiResponseModel.getStatus().getMessage(),
                                        Errors.DATA_NOT_FOUND
                                )
                        )
                )
                : Flux.just( EntitiesInstances.PSYCHOLOGY_CARD.get().generate( super.getErrorResponse() ) );
    }

    @SuppressWarnings( value = "возвращает данные по фотографии" )
    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_INITIAL" )
    public Mono< PsychologyCard > GET_PERSON_INITIAL_TOTAL_DATA (
            @lombok.NonNull final ApiResponseModel apiResponseModel
    ) {
        String base64url = apiResponseModel.getStatus().getMessage();
        token = base64url.split( "@" )[ 1 ];
        base64url = base64url.split( "@" )[ 0 ];

        return super.checkParam( base64url )
                ? FindFaceComponent
                .getInstance()
                .getPapilonList
                .apply( base64url )
                .filter( results -> isCollectionNotEmpty( results.getResults() ) )
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
                        : super.convert( EntitiesInstances.PSYCHOLOGY_CARD.get().generate( super.getErrorResponse() ) ) )
                .onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.completeError(
                                EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                EntitiesInstances.PSYCHOLOGY_CARD.get()
                        )
                ).onErrorReturn( super.completeError( EntitiesInstances.PSYCHOLOGY_CARD.get() ) )
                : super.convert( super.completeError( EntitiesInstances.PSYCHOLOGY_CARD.get() ) );
    }

    @SuppressWarnings( value = "возвращает данные по Пинфл" )
    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_BY_PINFL_INITIAL" )
    public Mono< PsychologyCard > GET_PERSON_TOTAL_DATA_BY_PINFL_INITIAL (
            @lombok.NonNull final ApiResponseModel apiResponseModel
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
                        throwable -> super.completeError(
                                EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                EntitiesInstances.PSYCHOLOGY_CARD.get()
                        )
                )
                : super.convert( super.completeError( EntitiesInstances.PSYCHOLOGY_CARD.get() ) )
                : super.convert( EntitiesInstances.PSYCHOLOGY_CARD.get().generate( super.getErrorResponse() ) );
    }

    @SuppressWarnings( value = "возвращает данные по номеру паспорта" )
    @MessageMapping ( value = "GET_PERSON_DATA_BY_PASSPORT_AND_BIRTHDATE_INITIAL" )
    public Mono< PsychologyCard > GET_PERSON_INITIAL_DATA_BY_PASSPORT_AND_BIRTHDATE (
            @lombok.NonNull final ApiResponseModel apiResponseModel
    ) {
        if ( !super.checkParam( apiResponseModel.getStatus().getMessage() ) ) {
            return super.convert( super.completeError( EntitiesInstances.PSYCHOLOGY_CARD.get() ) );
        }

        final WeakReference< String[] > strings = EntitiesInstances.generateWeakEntity(
                apiResponseModel.getStatus().getMessage().split( "_" )
        );

        super.logging( "Passport: " + strings.get()[0] + SPACE_WITH_DOUBLE_DOTS + strings.get()[1] );

        return Config.flag
                ? SerDes
                .getSerDes()
                .getGetModelForPassport()
                .apply( strings.get()[ 0 ] + strings.get()[ 1 ] )
                .flatMap( data -> {
                        super.clearReference( strings );
                        return SerDes
                                .getSerDes()
                                .getGetPsychologyCardByPinflInitial()
                                .apply( apiResponseModel.changeMessage( data.getData().getPerson().getPinpp() ) )
                                .map( psychologyCard -> psychologyCard.save( data ) );
                    }
                ).onErrorResume(
                        io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.completeError(
                                EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(),
                                EntitiesInstances.PSYCHOLOGY_CARD.get()
                        )
                ).onErrorReturn( super.completeError( EntitiesInstances.PSYCHOLOGY_CARD.get() ) )
                : super.convert( EntitiesInstances.PSYCHOLOGY_CARD.get().generate( super.getErrorResponse() ) );
    }
}