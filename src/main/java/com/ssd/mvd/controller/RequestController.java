package com.ssd.mvd.controller;

import java.util.List;
import java.util.Collections;

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
    public Mono< Boolean > ping () { return super.convert( Boolean.TRUE ); }

    @MessageMapping ( value = "GET_CAR_TONIROVKA" )
    public Mono< Tonirovka > getCarTonirovka ( final ApiResponseModel apiResponseModel ) {
        super.logging( "Request for: " + Methods.GET_TONIROVKA + " : " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetVehicleTonirovka()
                .apply( apiResponseModel.getStatus().getMessage() )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert( new Tonirovka( super.error.apply( throwable.getMessage(), 4 ) ) ) )
                .onErrorReturn( new Tonirovka( super.error.apply( Errors.SERVICE_WORK_ERROR.name(), 1 ) ) )
                : super.convert( new Tonirovka( super.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_BY_FIO" ) // возвращает данные по ФИО человека
    public Mono< PersonTotalDataByFIO > getPersonTotalDataByFIO ( final FIO fio ) {
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetPersonTotalDataByFIO()
                .apply( fio )
                .onErrorContinue( ( error, object ) -> super.logging(
                        error,
                        Methods.GET_PERSON_TOTAL_DATA_BY_FIO,
                        fio.toString() ) )
                .onErrorReturn( new PersonTotalDataByFIO( super.error.apply( Errors.SERVICE_WORK_ERROR.name(), 2 ) ) )
                : super.convert( new PersonTotalDataByFIO( super.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "GET_CAR_TOTAL_DATA" ) // возвращает данные по номеру машины
    public Mono< CarTotalData > getCarTotalData ( final ApiResponseModel apiResponseModel ) {
        super.logging( "Gos number: " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
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
                                .apply( apiResponseModel.getStatus().getMessage() ) )
                .map( CarTotalData::new )
                .flatMap( carTotalData -> super.checkData.test( 4, carTotalData )
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
                        .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                                throwable -> super.convert( new CarTotalData( super.error.apply( throwable.getMessage(), 4 ) ) ) )
                        : super.convert( carTotalData) )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert( new CarTotalData( super.error.apply( throwable.getMessage(), 4 ) ) ) )
                .onErrorReturn( new CarTotalData( super.error.apply( Errors.SERVICE_WORK_ERROR.name(), 1 ) ) )
                : super.convert( new CarTotalData( super.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA" ) // возвращает данные по фотографии
    public Mono< PsychologyCard > getPersonTotalData ( final ApiResponseModel apiResponseModel ) {
        String base64url = apiResponseModel.getStatus().getMessage();
        token = base64url.split( "@" )[ 1 ];
        base64url = base64url.split( "@" )[ 0 ];
        return super.checkParam.test( base64url )
                ? FindFaceComponent
                .getInstance()
                .getPapilonList
                .apply( base64url )
                .filter( results -> super.checkData.test( 5, results.getResults() ) )
                .flatMap( results -> SerDes
                        .getSerDes()
                        .getFlag()
                        ? results
                        .getResults()
                        .get( 0 )
                        .getCountry()
                        .equals( "УЗБЕКИСТАН" )
                        ? SerDes
                        .getSerDes()
                        .getGetPsychologyCardByImage()
                        .apply( results, apiResponseModel )
                        .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                                throwable -> super.convert( new PsychologyCard( super.error.apply(throwable.getMessage(), 4))) )
                        : SerDes
                        .getSerDes()
                        .getPsychologyCard(
                                token,
                                new PsychologyCard( results ),
                                apiResponseModel )
                        : super.convert( new PsychologyCard( super.getErrorResponse.get() ) ) )
                : super.convert( new PsychologyCard( super.error.apply( Errors.WRONG_PARAMS.name(), 2 ) ) ); }

    @MessageMapping ( value = "GET_PERSONAL_CADASTOR" ) // возвращает данные по номеру кадастра
    public Flux< PsychologyCard > getPersonalCadastor ( final ApiResponseModel apiResponseModel ) {
        super.logging( "Cadaster value: " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetCadaster()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMapMany( data -> super.checkData.test( 5, data.getPermanentRegistration() )
                        ? Flux.fromStream( data.getPermanentRegistration().stream() )
                        .flatMap( person -> SerDes
                                .getSerDes()
                                .getGetModelForPassport()
                                .apply( person.getPPsp(), person.getPDateBirth() )
                                .flatMap( data1 -> SerDes
                                        .getSerDes()
                                        .getGetPsychologyCardByData()
                                        .apply( data1, apiResponseModel )
                                        .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                                                throwable -> super.convert( new PsychologyCard( super.error.apply( throwable.getMessage(), 4 ) ) ) ) ) )
                        .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                                throwable -> super.convert( new PsychologyCard( super.error.apply( throwable.getMessage(), 4 ) ) ) )
                        .onErrorReturn( new PsychologyCard( super.error.apply( Errors.SERVICE_WORK_ERROR.name(), 1 ) ) )
                        : Flux.just( new PsychologyCard( super.error.apply( apiResponseModel.getStatus().getMessage(), 3 ) ) ) )
                : Flux.just( new PsychologyCard( super.getErrorResponse.get() ) ); }

    // возвращает данные по номеру машины в слуцчае если у человека роль IMITATION
    @MessageMapping ( value = "GET_CAR_TOTAL_DATA_BY_PINFL" )
    public Mono< CarTotalData > getCarTotalDataByPinfl ( final ApiResponseModel apiResponseModel ) {
        super.logging( "PINFL: " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetModelForCarList()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMap( modelForCarList -> super.checkObject.test( modelForCarList )
                        && super.checkData.test( 5, modelForCarList.getModelForCarList() )
                        ? this.getCarTotalData( ApiResponseModel
                        .builder()
                        .status( Status
                                .builder()
                                .message( modelForCarList
                                        .getModelForCarList()
                                        .get( 0 )
                                        .getPlateNumber() )
                                .build() )
                        .build() )
                        : this.getCarTotalData( ApiResponseModel
                        .builder()
                        .status( Status
                                .builder()
                                .message( "01Y456MA" )
                                .build() )
                        .build() ) )
                .onErrorResume( ReadTimeoutException.class,
                        throwable -> super.convert( new CarTotalData( super.error.apply( throwable.getMessage(), 4 ) ) ) )
                .onErrorReturn( new CarTotalData( super.error.apply( Errors.SERVICE_WORK_ERROR.name(), 1 ) ) )
                : super.convert( new CarTotalData( super.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_BY_PINFL" ) // возвращает данные по Пинфл
    public Mono< PsychologyCard > getPersonTotalDataByPinfl ( final ApiResponseModel apiResponseModel ) {
        super.logging( "PINFL: " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? super.checkParam.test( apiResponseModel.getStatus().getMessage() )
                ? SerDes
                .getSerDes()
                .getGetPsychologyCardByPinfl()
                .apply( apiResponseModel )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert( new PsychologyCard( super.error.apply( throwable.getMessage(), 4 ) ) ) )
                : super.convert( new PsychologyCard( super.error.apply( Errors.WRONG_PARAMS.name(), 2 ) ) )
                : super.convert( new PsychologyCard( super.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_BY_PASSPORT_AND_BIRTHDATE" ) // возвращает данные по номеру паспорта
    public Mono< PsychologyCard > getPersonDataByPassportSeriesAndBirthdate ( final ApiResponseModel apiResponseModel ) {
        if ( !super.checkParam.test( apiResponseModel.getStatus().getMessage() ) )
            return super.convert( new PsychologyCard( super.error.apply( Errors.WRONG_PARAMS.name(), 2 ) ) );
        final String[] strings = apiResponseModel.getStatus().getMessage().split( "_" );
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetModelForPassport()
                .apply( strings[ 0 ], strings[ 1 ] )
                .flatMap( data -> SerDes
                        .getSerDes()
                        .getGetPsychologyCardByData()
                        .apply( data, apiResponseModel ) )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert( new PsychologyCard( super.error.apply( throwable.getMessage(), 4 ) ) ) )
                .onErrorReturn( new PsychologyCard( super.error.apply( Errors.SERVICE_WORK_ERROR.name(), 2 ) ) )
                : super.convert( new PsychologyCard( super.getErrorResponse.get() ) ); }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // данные по авто

    @MessageMapping ( value = "GET_CAR_DATA_BY_GOS_NUMBER_INITIAL" ) // используется при запросе по номеру машины
    public Mono< CarTotalData > GET_CAR_DATA_BY_GOS_NUMBER_INITIAL ( final ApiResponseModel apiResponseModel ) {
        super.logging( "Gos number: " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetVehicleData()
                .apply( apiResponseModel.getStatus().getMessage() )
                .map( CarTotalData::new )
                .flatMap( carTotalData -> SerDes
                        .getSerDes()
                        .getGetPsychologyCardByPinflInitial()
                        .apply( apiResponseModel.changeMessage( carTotalData.getModelForCar().getPinpp() ) )
                        .map( carTotalData::save ) )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert( new CarTotalData( super.error.apply( throwable.getMessage(), 4 ) ) ) )
                .onErrorReturn( new CarTotalData( super.error.apply( Errors.SERVICE_WORK_ERROR.name(), 1 ) ) )
                : super.convert( new CarTotalData( super.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "GET_MODEL_FOR_CAR_LIST_INITIAL" ) // используется при запросе по пинфл человека
    public Mono< ModelForCarList > GET_MODEL_FOR_CAR_LIST_INITIAL ( final ApiResponseModel apiResponseModel ) {
        super.logging( "PINFL in GET_CAR_DATA_BY_PINFL_INITIAL: " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetModelForCarList()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMap( modelForCarList -> super.checkData.test( 6, modelForCarList )
                        ? SerDes
                        .getSerDes()
                        .getFindAllAboutCarList()
                        .apply( modelForCarList )
                        : super.convert( modelForCarList) )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert( new ModelForCarList( super.error.apply( throwable.getMessage(), 4 ) ) ) )
                .onErrorReturn( new ModelForCarList( super.error.apply( Errors.SERVICE_WORK_ERROR.name(), 1 ) ) )
                : super.convert( new ModelForCarList( super.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "GET_PERSON_FINES_FOR_DRIVING" ) // возвращает все штрафы от гаи по номеру машины
    public Mono< ViolationsList > GET_PERSON_FINES_FOR_DRIVING ( final ApiResponseModel apiResponseModel ) {
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetViolationList()
                .apply( apiResponseModel.getStatus().getMessage() )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert( new ViolationsList( super.error.apply( throwable.getMessage(), 4 ) ) ) )
                .onErrorReturn( new ViolationsList( super.error.apply( Errors.SERVICE_WORK_ERROR.name(), 1 ) ) )
                : super.convert( new ViolationsList( super.getErrorResponse.get() ) ); }

    // ---------------------------------------------------------------- дааные для человека

    @MessageMapping ( value = "GET_PINPP" )
    public Mono< Pinpp > getPINPP ( final ApiResponseModel apiResponseModel ) {
        super.logging( "Request for: " + Methods.GET_PINPP + " : " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetPinpp()
                .apply( apiResponseModel.getStatus().getMessage() )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert( new Pinpp( super.error.apply( throwable.getMessage(), 4 ) ) ) )
                .onErrorReturn( new Pinpp( super.error.apply( Errors.SERVICE_WORK_ERROR.name(), 1 ) ) )
                : super.convert( new Pinpp( super.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "GET_VIOLATION_LIST_BY_PINFL" ) // возвращает список правонарушений гражданина
    public Mono< List > GET_VIOLATION_LIST_BY_PINFL ( final ApiResponseModel apiResponseModel ) {
        return SerDes.getSerDes().getFlag()
                ? super.checkParam.test( apiResponseModel.getStatus().getMessage() )
                ? FindFaceComponent
                .getInstance()
                .getViolationListByPinfl
                .apply( apiResponseModel.getStatus().getMessage() )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class, throwable -> super.convert( Collections.emptyList() ) )
                .onErrorReturn( Collections.emptyList() )
                : super.convert( Collections.emptyList() )
                : super.convert( Collections.emptyList() ); }

    @MessageMapping ( value = "GET_CROSS_BOARDING" )
    public Mono< CrossBoardInfo > GET_PERSON_BOARD_CROSSING ( final ApiResponseModel apiResponseModel ) {
        super.logging( "Request for: " + Methods.GET_CROSS_BOARDING + " : " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetCrossBoardInfo()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMap( crossBoardInfo -> super.checkObject.test( crossBoardInfo.getData() )
                        && super.checkData.test( 5, crossBoardInfo.getData() )
                        && super.checkData.test( 5, crossBoardInfo.getData().get( 0 ).getCrossBoardList() )
                        ? SerDes
                        .getSerDes()
                        .getAnalyzeCrossData()
                        .apply( crossBoardInfo )
                        : super.convert( crossBoardInfo ) )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert( new CrossBoardInfo( super.error.apply( throwable.getMessage(), 4 ) ) ) )
                .onErrorReturn( new CrossBoardInfo( super.error.apply( Errors.SERVICE_WORK_ERROR.name(), 1 ) ) )
                : super.convert( new CrossBoardInfo( super.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "GET_TEMPORARY_OR_PERMANENT_REGISTRATION" ) // возвращает временную или постоянную прописку человека
    public Mono< ModelForAddress > GET_TEMPORARY_REGISTRATION (final ApiResponseModel apiResponseModel ) {
        super.logging( "pCitizen value in GET_TEMPORARY_OR_PERMANENT_REGISTRATION: " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetModelForAddress()
                .apply( apiResponseModel.getStatus().getMessage() )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert( new ModelForAddress( super.error.apply( throwable.getMessage(), 4 ) ) ) )
                .onErrorReturn( new ModelForAddress( super.error.apply( Errors.SERVICE_WORK_ERROR.name(), 1 ) ) )
                : super.convert( new ModelForAddress( super.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "GET_PERSONAL_CADASTOR_INITIAL" ) // возвращает данные по номеру кадастра
    public Flux< PsychologyCard > GET_PERSONAL_CADASTOR_INITIAL ( final ApiResponseModel apiResponseModel ) {
        super.logging( "Cadaster value in GET_PERSONALINITIAL_CADASTOR: " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetCadaster()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMapMany( data -> super.checkData.test( 5, data.getPermanentRegistration() )
                        ? Flux.fromStream( data
                                .getPermanentRegistration()
                                .stream() )
                        .flatMap( person -> SerDes
                                    .getSerDes()
                                    .getGetModelForPassport()
                                    .apply( person.getPPsp(), person.getPDateBirth() )
                                    .flatMap( data1 -> super.checkData.test( 3, data1 )
                                            ? SerDes
                                            .getSerDes()
                                            .getGetPsychologyCardByPinflInitial()
                                            .apply( apiResponseModel.changeMessage( data1.getData().getPerson().getPinpp() ) )
                                            .map( psychologyCard -> psychologyCard.save( data1 ) )
                                            .onErrorResume( ReadTimeoutException.class,
                                                    throwable -> super.convert( new PsychologyCard( super.error.apply( throwable.getMessage(), 4 ) ) ) )
                                            : super.convert( new PsychologyCard( super.error.apply( person.getPPsp() + " : " + person.getPDateBirth(), 3 ) ) ) ) )
                        .onErrorResume( ReadTimeoutException.class,
                                throwable -> super.convert( new PsychologyCard( super.error.apply( throwable.getMessage(), 4 ) ) ) )
                        .onErrorReturn( new PsychologyCard( super.error.apply( Errors.SERVICE_WORK_ERROR.name(), 1 ) ) )
                        : Flux.just( new PsychologyCard( super.error.apply( apiResponseModel.getStatus().getMessage(), 3 ) ) ) )
                : Flux.just( new PsychologyCard( super.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_INITIAL" ) // возвращает данные по фотографии
    public Mono< PsychologyCard > GET_PERSON_INITIAL_TOTAL_DATA ( final ApiResponseModel apiResponseModel ) {
        String base64url = apiResponseModel.getStatus().getMessage();
        token = base64url.split( "@" )[ 1 ];
        base64url = base64url.split( "@" )[ 0 ];
        return super.checkParam.test( base64url )
                ? FindFaceComponent
                .getInstance()
                .getPapilonList
                .apply( base64url )
                .filter( results -> super.checkData.test( 5, results.getResults() ) )
                .flatMap( results -> SerDes
                        .getSerDes()
                        .getFlag()
                        ? results
                        .getResults()
                        .get( 0 )
                        .getCountry()
                        .equals( "УЗБЕКИСТАН" )
                        ? super.convert( new PsychologyCard() )
                        .map( psychologyCard -> psychologyCard.save( results ) )
                        : SerDes
                        .getSerDes()
                        .getPsychologyCard( token, new PsychologyCard( results ), apiResponseModel )
                        : super.convert( new PsychologyCard( super.getErrorResponse.get() ) ) )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert( new PsychologyCard( super.error.apply( throwable.getMessage(), 4 ) ) ) )
                .onErrorReturn( new PsychologyCard( super.error.apply( Errors.SERVICE_WORK_ERROR.name(), 1 ) ) )
                : super.convert( new PsychologyCard( super.error.apply( Errors.WRONG_PARAMS.name(), 2 ) ) ); }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_BY_PINFL_INITIAL" ) // возвращает данные по Пинфл
    public Mono< PsychologyCard > GET_PERSON_TOTAL_DATA_BY_PINFL_INITIAL ( final ApiResponseModel apiResponseModel ) {
        super.logging( "PINFL in GET_PERSON_TOTAL_DATA_BY_PINFL_INITIAL: " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? super.checkParam.test( apiResponseModel.getStatus().getMessage() )
                ? SerDes
                .getSerDes()
                .getGetPsychologyCardByPinflInitial()
                .apply( apiResponseModel )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert( new PsychologyCard( super.error.apply( throwable.getMessage(), 4 ) ) ) )
                : super.convert( new PsychologyCard( super.error.apply(Errors.WRONG_PARAMS.name(), 2 ) ) )
                : super.convert( new PsychologyCard( super.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "GET_PERSON_DATA_BY_PASSPORT_AND_BIRTHDATE_INITIAL" ) // возвращает данные по номеру паспорта
    public Mono< PsychologyCard > GET_PERSON_INITIAL_DATA_BY_PASSPORT_AND_BIRTHDATE ( final ApiResponseModel apiResponseModel ) {
        if ( !super.checkParam.test( apiResponseModel.getStatus().getMessage() ) )
            return super.convert( new PsychologyCard( super.error.apply( Errors.WRONG_PARAMS.name(), 2 ) ) );
        final String[] strings = apiResponseModel.getStatus().getMessage().split( "_" );
        super.logging( "Passport: " + strings[0] + " : " + strings[1] );
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetModelForPassport()
                .apply( strings[ 0 ], strings[ 1 ] )
                .flatMap( data -> SerDes
                        .getSerDes()
                        .getGetPsychologyCardByPinflInitial()
                        .apply( apiResponseModel.changeMessage( data.getData().getPerson().getPinpp() ) )
                        .map( psychologyCard -> psychologyCard.save( data ) ) )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> super.convert( new PsychologyCard( super.error.apply( throwable.getMessage(), 4 ) ) ) )
                .onErrorReturn( new PsychologyCard( super.error.apply( Errors.SERVICE_WORK_ERROR.name(), 2 ) ) )
                : super.convert( new PsychologyCard( super.getErrorResponse.get() ) ); }
}