package com.ssd.mvd.controller;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.netty.handler.timeout.ReadTimeoutException;

import com.ssd.mvd.entity.*;
import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.entity.modelForGai.Tonirovka;
import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
import com.ssd.mvd.entity.modelForFioOfPerson.PersonTotalDataByFIO;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.handler.annotation.MessageMapping;

@RestController
public class RequestController {
    private static String token;

    @MessageMapping ( value = "ping" )
    public Mono< Boolean > ping () { return Mono.just( true ); }

    @MessageMapping ( value = "getPersonTotalDataByFIO" ) // возвращает данные по ФИО человека
    public Mono< PersonTotalDataByFIO > getPersonTotalDataByFIO ( FIO fio ) {
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetPersonTotalDataByFIO()
                .apply( fio )
                .onErrorContinue( ( error, object ) -> LogInspector
                        .getInstance()
                        .logging( error, Methods.GET_PERSON_TOTAL_DATA_BY_FIO, fio.toString() ) )
                .onErrorReturn( new PersonTotalDataByFIO( ErrorController
                        .getInstance()
                        .getGetServiceErrorResponse()
                        .apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                : Mono.just( new PersonTotalDataByFIO( ErrorController
                .getInstance()
                .getGetErrorResponse()
                .get() ) ); }

    @MessageMapping ( value = "GET_CAR_TOTAL_DATA" ) // возвращает данные по номеру машины
    public Mono< CarTotalData > getCarTotalData ( ApiResponseModel apiResponseModel ) {
        LogInspector
                .getInstance()
                .logging( "Gos number: " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? Mono.zip(
                        Mono.just( new Tonirovka() ),
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
                .flatMap( carTotalData -> DataValidationInspector
                        .getInstance()
                        .getCheckCarTotalData()
                        .test( carTotalData )
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
                                        ErrorController
                                                .getInstance()
                                                .getGetConnectionError()
                                                .apply( throwable.getMessage() ) ) ) )
                        : Mono.just( carTotalData ) )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> Mono.just( new CarTotalData(
                                ErrorController
                                                .getInstance()
                                .getGetConnectionError()
                                .apply( throwable.getMessage() ) ) ) )
                .onErrorReturn( new CarTotalData( ErrorController
                        .getInstance()
                        .getGetExternalServiceErrorResponse()
                        .apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                : Mono.just( new CarTotalData( ErrorController
                .getInstance()
                .getGetErrorResponse()
                .get() ) ); }

    // возвращает данные по номеру машины в слуцчае если у человека роль IMITATION
    @MessageMapping ( value = "GET_CAR_TOTAL_DATA_BY_PINFL" )
    public Mono< CarTotalData > getCarTotalDataByPinfl ( ApiResponseModel apiResponseModel ) {
        LogInspector
                .getInstance()
                .logging( "PINFL: " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetModelForCarList()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMap( modelForCarList -> modelForCarList != null
                        && DataValidationInspector
                        .getInstance()
                        .getCheckList()
                        .test( modelForCarList.getModelForCarList() )
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
                        throwable -> Mono.just( new CarTotalData(
                                ErrorController
                                                .getInstance()
                                        .getGetConnectionError()
                                        .apply( throwable.getMessage() ) ) ) )
                .onErrorReturn( new CarTotalData( ErrorController
                        .getInstance()
                        .getGetExternalServiceErrorResponse()
                        .apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                : Mono.just( new CarTotalData( ErrorController
                .getInstance()
                .getGetErrorResponse()
                .get() ) ); }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA" ) // возвращает данные по фотографии
    public Mono< PsychologyCard > getPersonTotalData ( ApiResponseModel apiResponseModel ) {
        String base64url = apiResponseModel.getStatus().getMessage();
        token = base64url.split( "@" )[ 1 ];
        base64url = base64url.split( "@" )[ 0 ];
        return DataValidationInspector
                .getInstance()
                .getCheckParam()
                .test( base64url )
                ? FindFaceComponent
                .getInstance()
                .getPapilonList( base64url )
                .filter( results -> DataValidationInspector
                        .getInstance()
                        .getCheckList()
                        .test( results.getResults() ) )
                .flatMap( results ->
                        SerDes
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
                                        throwable -> Mono.just( new PsychologyCard(
                                                ErrorController
                                                        .getInstance()
                                                        .getGetConnectionError()
                                                        .apply( throwable.getMessage() ) ) ) )
                                : SerDes
                                .getSerDes()
                                .getPsychologyCard(
                                        new PsychologyCard( results ),
                                        token,
                                        apiResponseModel )
                                : Mono.just( new PsychologyCard( ErrorController
                                .getInstance()
                                .getGetErrorResponse()
                                .get() ) ) )
                : Mono.just( new PsychologyCard( ErrorController
                        .getInstance()
                .getGetServiceErrorResponse()
                .apply( Errors.WRONG_PARAMS.name() ) ) ); }

    @MessageMapping ( value = "GET_PERSONAL_CADASTOR" ) // возвращает данные по номеру кадастра
    public Flux< PsychologyCard > getPersonalCadastor ( ApiResponseModel apiResponseModel ) {
        LogInspector
                .getInstance()
                .logging( "Cadaster value: " + apiResponseModel.getStatus().getMessage() );
        if ( !SerDes.getSerDes().getFlag() ) return Flux.just( new PsychologyCard( ErrorController
                .getInstance()
                .getGetErrorResponse()
                .get() ) );
        return SerDes
                .getSerDes()
                .getGetCadaster()
                .apply( apiResponseModel.getStatus().getMessage() )
                .flatMapMany( data -> DataValidationInspector
                        .getInstance()
                        .getCheckList()
                        .test( data.getPermanentRegistration() )
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
                                                        ErrorController
                                                                .getInstance()
                                                                .getGetConnectionError()
                                                                .apply( throwable.getMessage() ) ) ) ) ) )
                        .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                                throwable -> Mono.just( new PsychologyCard(
                                        ErrorController
                                                .getInstance()
                                                .getGetConnectionError()
                                                .apply( throwable.getMessage() ) ) ) )
                        .onErrorReturn( new PsychologyCard( ErrorController
                                .getInstance()
                                .getGetExternalServiceErrorResponse()
                                .apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                        : Flux.just( new PsychologyCard(
                                ErrorController
                                .getInstance()
                                .getGetDataNotFoundErrorResponse()
                                .apply( apiResponseModel.getStatus().getMessage() ) ) ) ); }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_BY_PINFL" ) // возвращает данные по Пинфл
    public Mono< PsychologyCard > getPersonTotalDataByPinfl ( ApiResponseModel apiResponseModel ) {
        LogInspector
                .getInstance()
                .logging( "PINFL: " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? DataValidationInspector
                .getInstance()
                .getCheckParam()
                .test( apiResponseModel
                        .getStatus()
                        .getMessage() )
                ? SerDes
                .getSerDes()
                .getGetPsychologyCardByPinfl()
                .apply( apiResponseModel )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> Mono.just( new PsychologyCard(
                                ErrorController
                                        .getInstance()
                                        .getGetConnectionError()
                                        .apply( throwable.getMessage() ) ) ) )
                : Mono.just( new PsychologyCard(
                        ErrorController
                        .getInstance()
                        .getGetServiceErrorResponse()
                        .apply( Errors.WRONG_PARAMS.name() ) ) )
                : Mono.just( new PsychologyCard( ErrorController
                .getInstance()
                .getGetErrorResponse()
                .get() ) ); }

    @MessageMapping ( value = "GET_PERSON_TOTAL_DATA_BY_PASSPORT_AND_BIRTHDATE" ) // возвращает данные по номеру паспорта
    public Mono< PsychologyCard > getPersonDataByPassportSeriesAndBirthdate ( ApiResponseModel apiResponseModel ) {
        if ( !DataValidationInspector
                .getInstance()
                .getCheckParam()
                .test( apiResponseModel
                        .getStatus()
                        .getMessage() ) )
            return Mono.just(
                    new PsychologyCard(
                            ErrorController
                                    .getInstance()
                                    .getGetServiceErrorResponse()
                                    .apply( Errors.WRONG_PARAMS.name() ) ) );
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
                        throwable -> Mono.just( new PsychologyCard(
                                ErrorController
                                        .getInstance()
                                        .getGetConnectionError()
                                        .apply( throwable.getMessage() ) ) ) )
                .onErrorReturn( new PsychologyCard( ErrorController
                        .getInstance()
                        .getGetServiceErrorResponse()
                        .apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                : Mono.just( new PsychologyCard( ErrorController
                .getInstance()
                .getGetErrorResponse()
                .get() ) ); }
}