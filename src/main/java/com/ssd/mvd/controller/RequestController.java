package com.ssd.mvd.controller;

import java.util.List;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import com.ssd.mvd.entity.*;
import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.entity.modelForCadastr.Person;
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

    private final Supplier< ErrorResponse > getWrongParamResponse = () -> ErrorResponse
            .builder()
            .message( "Wrong params" )
            .errors( Errors.WRONG_PARAMS )
            .build();

    @MessageMapping ( value = "ping" )
    public Mono< Boolean > ping () { return Mono.just( true ); }

    @MessageMapping ( value = "getFamilyMembersData" )
    public Mono< Results > getFamilyMembersData ( String pinfl ) {
        return SerDes.getSerDes().getFlag()
                ? FindFaceComponent
                .getInstance()
                .getFamilyMembersData( pinfl )
                .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                        error.getMessage(), object ) ) )
                .onErrorReturn( new Results( SerDes.getSerDes().getGetServiceErrorResponse().apply( "" ) ) )
                : Mono.just( new Results( this.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "getPersonTotalDataByFIO" ) // возвращает данные по ФИО человека
    public Mono< PersonTotalDataByFIO > getPersonTotalDataByFIO ( FIO fio ) {
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetPersonTotalDataByFIO()
                .apply( fio )
                .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                        error.getMessage(), object ) ) )
                .onErrorReturn( new PersonTotalDataByFIO( SerDes.getSerDes().getGetServiceErrorResponse().apply( "" ) ) )
                : Mono.just( new PersonTotalDataByFIO( this.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "getCarTotalData" ) // возвращает данные по номеру машины
    public Mono< CarTotalData > getCarTotalData ( ApiResponseModel apiResponseModel ) {
        log.info( "Gos number: " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? Mono.zip(
                        Mono.fromCallable( () -> SerDes
                                        .getSerDes()
                                        .getGetVehicleTonirovka()
                                        .apply( apiResponseModel.getStatus().getMessage() ) )
                                .subscribeOn( Schedulers.boundedElastic() ),
                        Mono.fromCallable( () -> SerDes
                                        .getSerDes()
                                        .getGetVehicleData()
                                        .apply( apiResponseModel.getStatus().getMessage() ) )
                                .subscribeOn( Schedulers.boundedElastic() ),
                        Mono.fromCallable( () -> SerDes
                                        .getSerDes()
                                        .getGetDoverennostList()
                                        .apply( apiResponseModel.getStatus().getMessage() ) )
                                .subscribeOn( Schedulers.boundedElastic() ),
                        Mono.fromCallable( () -> SerDes
                                        .getSerDes()
                                        .getInsurance()
                                        .apply( apiResponseModel.getStatus().getMessage() ) )
                                .subscribeOn( Schedulers.boundedElastic() ),
                        Mono.fromCallable( () -> SerDes
                                        .getSerDes()
                                        .getGetViolationList()
                                        .apply( apiResponseModel.getStatus().getMessage() ) )
                                .subscribeOn( Schedulers.boundedElastic() ) )
                .map( CarTotalData::new )
                .flatMap( carTotalData -> carTotalData.getModelForCar() != null
                        && carTotalData.getModelForCar().getPinpp() != null
                        && !carTotalData.getModelForCar().getPinpp().isEmpty()
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
                        .map( psychologyCard -> {
                            carTotalData.setPsychologyCard( psychologyCard );
                            return carTotalData; } )
                        : Mono.just( carTotalData ) )
                .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                        error.getMessage(), object ) ) )
                .onErrorReturn( new CarTotalData( SerDes.getSerDes().getGetServiceErrorResponse().apply( "" ) ) )
                : Mono.just( new CarTotalData( this.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "getPersonTotalData" ) // возвращает данные по фотографии
    public Mono< PsychologyCard > getPersonTotalData ( ApiResponseModel apiResponseModel ) {
        String base64url = apiResponseModel.getStatus().getMessage();
        token = base64url.split( "@" )[ 1 ];
        base64url = base64url.split( "@" )[ 0 ];
        return base64url != null && base64url.length() > 0
                ? FindFaceComponent
                .getInstance()
                .getPapilonList( base64url )
                .filter( value -> value.getResults() != null
                        && value.getResults().size() > 0 )
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
                                : SerDes
                                .getSerDes()
                                .getPsychologyCard(
                                        new PsychologyCard( results ),
                                        token,
                                        apiResponseModel )
                                : Mono.just( new PsychologyCard( this.getErrorResponse.get() ) ) )
                : Mono.just( new PsychologyCard( this.getWrongParamResponse.get() ) ); }

    @MessageMapping ( value = "getPersonalCadastor" ) // возвращает данные по номеру кадастра
    public Flux< PsychologyCard > getPersonalCadastor ( ApiResponseModel apiResponseModel ) {
        log.info( "Request for cadastre: " + apiResponseModel.getStatus().getMessage() );
        if ( !SerDes.getSerDes().getFlag() ) return Flux.just( new PsychologyCard( this.getErrorResponse.get() ) );
        final List< Person > personList = SerDes // находим данные кадастра
                .getSerDes()
                .getDeserialize()
                .apply( apiResponseModel.getStatus().getMessage() )
                .getPermanentRegistration();
        return personList != null && !personList.isEmpty() // проверяем что все ок
                ? Flux.fromStream( personList
                        .stream()
                        .parallel() )
                .parallel( personList.size() ) // создаем паралеллизм
                .runOn( Schedulers.parallel() )
                .flatMap( person -> SerDes
                        .getSerDes()
                        .getGetPsychologyCardByData()
                        .apply( SerDes
                                .getSerDes()
                                .getGetPassportData()
                                .apply(
                                        person.getPPsp(),
                                        person.getPDateBirth() ),
                                apiResponseModel ) )
                .sequential()
                .publishOn( Schedulers.single() )
                .onErrorContinue( ( error, object ) -> log.error( "Error: {} and reason: {}: ",
                        error.getMessage(), object ) )
                .onErrorReturn( new PsychologyCard( SerDes
                        .getSerDes()
                        .getGetServiceErrorResponse()
                        .apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                : Flux.just( new PsychologyCard( SerDes
                        .getSerDes()
                        .getGetDataNotFoundErrorResponse()
                        .apply( apiResponseModel.getStatus().getMessage() ) ) ); }

    @MessageMapping ( value = "getPersonTotalDataByPinfl" ) // возвращает данные по Пинфл
    public Mono< PsychologyCard > getPersonTotalDataByPinfl ( ApiResponseModel apiResponseModel ) {
        return SerDes.getSerDes().getFlag()
                ? apiResponseModel.getStatus().getMessage() != null
                && apiResponseModel.getStatus().getMessage().length() > 0
                ? SerDes
                .getSerDes()
                .getGetPsychologyCardByPinfl()
                .apply( apiResponseModel )
                .onErrorContinue( ( error, object ) -> log.error( "Error: {} and reason: {}: ",
                        error.getMessage(), object ) )
                .onErrorReturn( new PsychologyCard( SerDes.getSerDes().getGetServiceErrorResponse().apply( "" ) ) )
                : Mono.just( new PsychologyCard( this.getWrongParamResponse.get() ) )
                : Mono.just( new PsychologyCard( this.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "getPersonDataByPassportSeriesAndBirthdate" ) // возвращает данные по номеру паспорта
    public Mono< PsychologyCard > getPersonDataByPassportSeriesAndBirthdate ( ApiResponseModel apiResponseModel ) {
        if ( apiResponseModel
                .getStatus()
                .getMessage() == null ) return Mono.just(
                        new PsychologyCard( SerDes
                                .getSerDes()
                                .getGetServiceErrorResponse()
                                .apply( Errors.WRONG_PARAMS.name() ) ) );
        String[] strings = apiResponseModel.getStatus().getMessage().split( "_" );
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetPsychologyCardByData()
                .apply( SerDes
                        .getSerDes()
                        .getGetPassportData()
                        .apply( strings[ 0 ], strings[ 1 ] ),
                        apiResponseModel )
                .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                        error.getMessage(), object ) ) )
                .onErrorReturn( new PsychologyCard( SerDes
                        .getSerDes()
                        .getGetServiceErrorResponse()
                        .apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                : Mono.just( new PsychologyCard( this.getErrorResponse.get() ) ); }
}