package com.ssd.mvd.controller;

import java.util.List;
import java.util.ArrayList;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.netty.handler.timeout.ReadTimeoutException;

import com.ssd.mvd.entity.*;
import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
import com.ssd.mvd.entity.modelForFioOfPerson.PersonTotalDataByFIO;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.handler.annotation.MessageMapping;

@RestController
public class RequestController extends LogInspector {
    private static String token;

    @MessageMapping ( value = "ping" )
    public Mono< Boolean > ping () { return Mono.just( true ); }

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
                .onErrorReturn( new PersonTotalDataByFIO( super.getServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                : Mono.just( new PersonTotalDataByFIO( super.getErrorResponse.get() ) ); }

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
                .flatMap( carTotalData -> super.getCheckCarTotalData().test( carTotalData )
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
                                throwable -> Mono.just( new CarTotalData(
                                        super.getConnectionError.apply( throwable.getMessage() ) ) ) )
                        : Mono.just( carTotalData ) )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> Mono.just( new CarTotalData(
                                super.getConnectionError.apply( throwable.getMessage() ) ) ) )
                .onErrorReturn( new CarTotalData(
                        super.getExternalServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                : Mono.just( new CarTotalData( super.getErrorResponse.get() ) ); }

    // возвращает данные по номеру машины в слуцчае если у человека роль IMITATION
    @MessageMapping ( value = "GET_CAR_TOTAL_DATA_BY_PINFL" )
    public Mono< CarTotalData > getCarTotalDataByPinfl ( final ApiResponseModel apiResponseModel ) {
        super.logging( "PINFL: " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetModelForCarList()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMap( modelForCarList -> modelForCarList != null
                        && super.getCheckList().test( modelForCarList.getModelForCarList() )
                        ? this.getCarTotalData(
                                ApiResponseModel
                                        .builder()
                                        .status( Status
                                                .builder()
                                                .message( modelForCarList
                                                        .getModelForCarList()
                                                        .get( 0 )
                                                        .getPlateNumber() )
                                                .build() )
                                        .build() )
                        : this.getCarTotalData(
                                ApiResponseModel
                                        .builder()
                                        .status( Status
                                                .builder()
                                                .message( "01Y456MA" )
                                                .build() )
                                        .build() ) )
                .onErrorResume( ReadTimeoutException.class,
                        throwable -> Mono.just( new CarTotalData( super.getConnectionError.apply( throwable.getMessage() ) ) ) )
                .onErrorReturn( new CarTotalData( super.getExternalServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                : Mono.just( new CarTotalData( super.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA" ) // возвращает данные по фотографии
    public Mono< PsychologyCard > getPersonTotalData ( final ApiResponseModel apiResponseModel ) {
        String base64url = apiResponseModel.getStatus().getMessage();
        token = base64url.split( "@" )[ 1 ];
        base64url = base64url.split( "@" )[ 0 ];
        return super.getCheckParam().test( base64url )
                ? FindFaceComponent
                .getInstance()
                .getPapilonList( base64url )
                .filter( results -> super.getCheckList().test( results.getResults() ) )
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
                                throwable -> Mono.just( new PsychologyCard( super.getConnectionError.apply( throwable.getMessage() ) ) ) )
                        : SerDes
                        .getSerDes()
                        .getPsychologyCard(
                                new PsychologyCard( results ),
                                token,
                                apiResponseModel )
                        : Mono.just( new PsychologyCard( super.getErrorResponse.get() ) ) )
                : Mono.just( new PsychologyCard( super.getServiceErrorResponse.apply( Errors.WRONG_PARAMS.name() ) ) ); }

    @MessageMapping ( value = "GET_PERSONAL_CADASTOR" ) // возвращает данные по номеру кадастра
    public Flux< PsychologyCard > getPersonalCadastor ( final ApiResponseModel apiResponseModel ) {
        super.logging( "Cadaster value: " + apiResponseModel.getStatus().getMessage() );
        if ( !SerDes.getSerDes().getFlag() ) return Flux.just( new PsychologyCard( super.getErrorResponse.get() ) );
        return SerDes
                .getSerDes()
                .getGetCadaster()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMapMany( data -> super.getCheckList().test( data.getPermanentRegistration() )
                        ? Flux.fromStream( data
                                .getPermanentRegistration()
                                .stream() )
                        .flatMap( person -> SerDes
                                .getSerDes()
                                .getGetModelForPassport()
                                .apply( person.getPPsp(), person.getPDateBirth() )
                                .flatMap( data1 -> SerDes
                                        .getSerDes()
                                        .getGetPsychologyCardByData()
                                        .apply( data1, apiResponseModel )
                                        .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                                                throwable -> Mono.just( new PsychologyCard(
                                                        super.getConnectionError.apply( throwable.getMessage() ) ) ) ) ) )
                        .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                                throwable -> Mono.just( new PsychologyCard( super.getConnectionError.apply( throwable.getMessage() ) ) ) )
                        .onErrorReturn( new PsychologyCard( super.getExternalServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                        : Flux.just( new PsychologyCard( super.getDataNotFoundErrorResponse.apply( apiResponseModel.getStatus().getMessage() ) ) ) ); }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_BY_PINFL" ) // возвращает данные по Пинфл
    public Mono< PsychologyCard > getPersonTotalDataByPinfl ( final ApiResponseModel apiResponseModel ) {
        super.logging( "PINFL: " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? super.getCheckParam().test( apiResponseModel.getStatus().getMessage() )
                ? SerDes
                .getSerDes()
                .getGetPsychologyCardByPinfl()
                .apply( apiResponseModel )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> Mono.just( new PsychologyCard( super.getConnectionError.apply( throwable.getMessage() ) ) ) )
                : Mono.just( new PsychologyCard( super.getServiceErrorResponse.apply( Errors.WRONG_PARAMS.name() ) ) )
                : Mono.just( new PsychologyCard( super.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_BY_PASSPORT_AND_BIRTHDATE" ) // возвращает данные по номеру паспорта
    public Mono< PsychologyCard > getPersonDataByPassportSeriesAndBirthdate ( final ApiResponseModel apiResponseModel ) {
        if ( !super.getCheckParam().test( apiResponseModel.getStatus().getMessage() ) )
            return Mono.just( new PsychologyCard( super.getServiceErrorResponse.apply( Errors.WRONG_PARAMS.name() ) ) );
        String[] strings = apiResponseModel.getStatus().getMessage().split( "_" );
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
                        throwable -> Mono.just( new PsychologyCard( super.getConnectionError.apply( throwable.getMessage() ) ) ) )
                .onErrorReturn( new PsychologyCard( super.getServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                : Mono.just( new PsychologyCard( super.getErrorResponse.get() ) ); }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @MessageMapping ( value = "GET_VIOLATION_LIST_BY_PINFL" )
    public Mono< List > GET_VIOLATION_LIST_BY_PINFL ( final ApiResponseModel apiResponseModel ) {
        return SerDes.getSerDes().getFlag()
                ? super.checkParam.test( apiResponseModel.getStatus().getMessage() )
                ? FindFaceComponent
                .getInstance()
                .getViolationListByPinfl( apiResponseModel.getStatus().getMessage() )
                : Mono.just( new ArrayList() )
                : Mono.just( new ArrayList() ); }

    @MessageMapping ( value = "GET_CAR_DATA_BY_GOS_NUMBER_INITIAL" ) // используется при запросе по номеру машины
    public Mono< CarTotalData > GET_CAR_DATA_BY_GOS_NUMBER_INITIAL ( final ApiResponseModel apiResponseModel ) {
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
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> Mono.just( new CarTotalData(
                                super.getConnectionError.apply( throwable.getMessage() ) ) ) )
                .onErrorReturn( new CarTotalData(
                        super.getExternalServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                : Mono.just( new CarTotalData( super.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "GET_MODEL_FOR_CAR_LIST_INITIAL" ) // используется при запросе по пинфл человека
    public Mono< ModelForCarList > GET_MODEL_FOR_CAR_LIST_INITIAL ( final ApiResponseModel apiResponseModel ) {
        super.logging( "PINFL in GET_CAR_DATA_BY_PINFL_INITIAL: " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetModelForCarList()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMap( modelForCarList -> super.getCheckCarList().test( modelForCarList )
                        ? SerDes
                        .getSerDes()
                        .getFindAllAboutCarList()
                        .apply( modelForCarList )
                        : Mono.just( modelForCarList ) )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> Mono.just( new ModelForCarList(
                                super.getConnectionError.apply( throwable.getMessage() ) ) ) )
                .onErrorReturn( new ModelForCarList(
                        super.getExternalServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                : Mono.just( new ModelForCarList( super.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "GET_PERSONAL_CADASTOR_INITIAL" ) // возвращает данные по номеру кадастра
    public Flux< PsychologyCard > GET_PERSONALINITIAL_CADASTOR ( final ApiResponseModel apiResponseModel ) {
        super.logging( "Cadaster value in GET_PERSONALINITIAL_CADASTOR: " + apiResponseModel.getStatus().getMessage() );
        if ( !SerDes.getSerDes().getFlag() ) return Flux.just( new PsychologyCard( super.getErrorResponse.get() ) );
        return SerDes
                .getSerDes()
                .getGetCadaster()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMapMany( data -> super.getCheckList().test( data.getPermanentRegistration() )
                        ? Flux.fromStream( data
                                .getPermanentRegistration()
                                .stream() )
                        .flatMap( person -> SerDes
                                .getSerDes()
                                .getGetModelForPassport()
                                .apply( person.getPPsp(), person.getPDateBirth() )
                                .flatMap( data1 -> SerDes
                                        .getSerDes()
                                        .getGetPsychologyCardByDataInitial()
                                        .apply( data1, apiResponseModel )
                                        .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                                                throwable -> Mono.just( new PsychologyCard(
                                                        super.getConnectionError.apply( throwable.getMessage() ) ) ) ) ) )
                        .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                                throwable -> Mono.just( new PsychologyCard( super.getConnectionError.apply( throwable.getMessage() ) ) ) )
                        .onErrorReturn( new PsychologyCard( super.getExternalServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                        : Flux.just( new PsychologyCard( super.getDataNotFoundErrorResponse.apply( apiResponseModel.getStatus().getMessage() ) ) ) ); }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_INITIAL" ) // возвращает данные по фотографии
    public Mono< PsychologyCard > GET_PERSON_INITIAL_TOTAL_DATA ( final ApiResponseModel apiResponseModel ) {
        String base64url = apiResponseModel.getStatus().getMessage();
        token = base64url.split( "@" )[ 1 ];
        base64url = base64url.split( "@" )[ 0 ];
        return super.getCheckParam().test( base64url )
                ? FindFaceComponent
                .getInstance()
                .getPapilonList( base64url )
                .filter( results -> super.getCheckList().test( results.getResults() ) )
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
                        .getGetPsychologyCardByImageInitial()
                        .apply( results, apiResponseModel )
                        .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                                throwable -> Mono.just( new PsychologyCard( super.getConnectionError.apply( throwable.getMessage() ) ) ) )
                        : SerDes
                        .getSerDes()
                        .getPsychologyCard(
                                new PsychologyCard( results ),
                                token,
                                apiResponseModel )
                        : Mono.just( new PsychologyCard( super.getErrorResponse.get() ) ) )
                : Mono.just( new PsychologyCard( super.getServiceErrorResponse.apply( Errors.WRONG_PARAMS.name() ) ) ); }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_BY_PINFL_INITIAL" ) // возвращает данные по Пинфл
    public Mono< PsychologyCard > GET_PERSON_TOTAL_DATA_BY_PINFL_INITIAL ( final ApiResponseModel apiResponseModel ) {
        super.logging( "PINFL in GET_PERSON_TOTAL_DATA_BY_PINFL_INITIAL: " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? super.getCheckParam().test( apiResponseModel.getStatus().getMessage() )
                ? SerDes
                .getSerDes()
                .getGetPsychologyCardByPinflInitial()
                .apply( apiResponseModel )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> Mono.just( new PsychologyCard( super.getConnectionError.apply( throwable.getMessage() ) ) ) )
                : Mono.just( new PsychologyCard( super.getServiceErrorResponse.apply( Errors.WRONG_PARAMS.name() ) ) )
                : Mono.just( new PsychologyCard( super.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "GET_PERSON_DATA_BY_PASSPORT_AND_BIRTHDATE_INITIAL" ) // возвращает данные по номеру паспорта
    public Mono< PsychologyCard > GET_PERSON_INITIAL_DATA_BY_PASSPORT_AND_BIRTHDATE ( final ApiResponseModel apiResponseModel ) {
        if ( !super.getCheckParam().test( apiResponseModel.getStatus().getMessage() ) )
            return Mono.just( new PsychologyCard( super.getServiceErrorResponse.apply( Errors.WRONG_PARAMS.name() ) ) );
        String[] strings = apiResponseModel.getStatus().getMessage().split( "_" );
        super.logging( "Passport: " + strings[0] + " : " + strings[1] );
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetModelForPassport()
                .apply( strings[ 0 ], strings[ 1 ] )
                .flatMap( data -> SerDes
                        .getSerDes()
                        .getGetPsychologyCardByDataInitial()
                        .apply( data, apiResponseModel ) )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> Mono.just( new PsychologyCard( super.getConnectionError.apply( throwable.getMessage() ) ) ) )
                .onErrorReturn( new PsychologyCard( super.getServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                : Mono.just( new PsychologyCard( super.getErrorResponse.get() ) ); }
}