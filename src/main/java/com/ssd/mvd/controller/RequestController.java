package com.ssd.mvd.controller;

import lombok.extern.slf4j.Slf4j;
import java.util.function.Supplier;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.ssd.mvd.entity.*;
import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
import com.ssd.mvd.entity.modelForFioOfPerson.PersonTotalDataByFIO;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.handler.annotation.MessageMapping;

@Slf4j
@RestController
public class RequestController {
    private static String token;

    private final Supplier< ErrorResponse > getErrorResponse = () -> ErrorResponse
            .builder()
            .message( "GAI token is unavailable" )
            .errors( Errors.GAI_TOKEN_ERROR )
            .build();

    @MessageMapping ( value = "ping" )
    public Mono< Boolean > ping () { return Mono.just( true ); }

    @MessageMapping ( value = "getPersonTotalDataByFIO" ) // возвращает данные по ФИО человека
    public Mono< PersonTotalDataByFIO > getPersonTotalDataByFIO ( FIO fio ) {
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetPersonTotalDataByFIO()
                .apply( fio )
                .onErrorContinue( ( error, object ) -> log.error( "Error: {} and reason: {}: ",
                        error.getMessage(), object ) )
                .onErrorReturn( new PersonTotalDataByFIO( SerDes
                        .getSerDes()
                        .getGetServiceErrorResponse()
                        .apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                : Mono.just( new PersonTotalDataByFIO( this.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "getCarTotalData" ) // возвращает данные по номеру машины
    public Mono< CarTotalData > getCarTotalData ( ApiResponseModel apiResponseModel ) {
        log.info( "Gos number: " + apiResponseModel.getStatus().getMessage() );
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
                                throwable -> Mono.just( new CarTotalData( SerDes
                                        .getSerDes()
                                        .getGetConnectionError()
                                        .apply( throwable.getMessage() ) ) ) )
                        : Mono.just( carTotalData ) )
                .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                        throwable -> Mono.just( new CarTotalData(
                                SerDes
                                .getSerDes()
                                .getGetConnectionError()
                                .apply( throwable.getMessage() ) ) ) )
                .onErrorReturn( new CarTotalData( SerDes
                        .getSerDes()
                        .getGetExternalServiceErrorResponse()
                        .apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                : Mono.just( new CarTotalData( this.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "getPersonTotalData" ) // возвращает данные по фотографии
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
                                        throwable -> Mono.just( new PsychologyCard( SerDes
                                                .getSerDes()
                                                .getGetConnectionError()
                                                .apply( throwable.getMessage() ) ) ) )
                                : SerDes
                                .getSerDes()
                                .getPsychologyCard(
                                        new PsychologyCard( results ),
                                        token,
                                        apiResponseModel )
                                : Mono.just( new PsychologyCard( this.getErrorResponse.get() ) ) )
                : Mono.just( new PsychologyCard( SerDes
                .getSerDes()
                .getGetServiceErrorResponse()
                .apply( Errors.WRONG_PARAMS.name() ) ) ); }

    @MessageMapping ( value = "getPersonalCadastor" ) // возвращает данные по номеру кадастра
    public Flux< PsychologyCard > getPersonalCadastor ( ApiResponseModel apiResponseModel ) {
        log.info( apiResponseModel.getStatus().getMessage() );
        if ( !SerDes.getSerDes().getFlag() ) return Flux.just( new PsychologyCard( this.getErrorResponse.get() ) );
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
                                                        SerDes
                                                        .getSerDes()
                                                        .getGetConnectionError()
                                                        .apply( throwable.getMessage() ) ) ) ) ) )
                        .onErrorResume( io.netty.handler.timeout.ReadTimeoutException.class,
                                throwable -> Mono.just( new PsychologyCard(
                                        SerDes
                                        .getSerDes()
                                        .getGetConnectionError()
                                        .apply( throwable.getMessage() ) ) ) )
                        .onErrorReturn( new PsychologyCard( SerDes
                                .getSerDes()
                                .getGetExternalServiceErrorResponse()
                                .apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                        : Flux.just( new PsychologyCard(
                                SerDes
                                .getSerDes()
                                .getGetDataNotFoundErrorResponse()
                                .apply( apiResponseModel.getStatus().getMessage() ) ) ) ); }

    @MessageMapping ( value = "getPersonTotalDataByPinfl" ) // возвращает данные по Пинфл
    public Mono< PsychologyCard > getPersonTotalDataByPinfl ( ApiResponseModel apiResponseModel ) {
        log.info( "Pinfl: " + apiResponseModel
                .getUser()
                .getPinfl() );
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
                        throwable -> Mono.just( new PsychologyCard( SerDes
                                .getSerDes()
                                .getGetConnectionError()
                                .apply( throwable.getMessage() ) ) ) )
                : Mono.just( new PsychologyCard(
                        SerDes
                        .getSerDes()
                        .getGetServiceErrorResponse()
                        .apply( Errors.WRONG_PARAMS.name() ) ) )
                : Mono.just( new PsychologyCard( this.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "getPersonDataByPassportSeriesAndBirthdate" ) // возвращает данные по номеру паспорта
    public Mono< PsychologyCard > getPersonDataByPassportSeriesAndBirthdate ( ApiResponseModel apiResponseModel ) {
        if ( !DataValidationInspector
                .getInstance()
                .getCheckParam()
                .test( apiResponseModel
                        .getStatus()
                        .getMessage() ) )
            return Mono.just(
                    new PsychologyCard(
                                SerDes
                                .getSerDes()
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
                        throwable -> Mono.just( new PsychologyCard( SerDes
                                .getSerDes()
                                .getGetConnectionError()
                                .apply( throwable.getMessage() ) ) ) )
                .onErrorReturn( new PsychologyCard( SerDes
                        .getSerDes()
                        .getGetServiceErrorResponse()
                        .apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                : Mono.just( new PsychologyCard( this.getErrorResponse.get() ) ); }
}