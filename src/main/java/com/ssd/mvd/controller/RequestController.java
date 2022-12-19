package com.ssd.mvd.controller;

import lombok.extern.slf4j.Slf4j;
import java.util.function.Supplier;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.ssd.mvd.entity.*;
import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.entity.modelForCadastr.Data;
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
                .onErrorReturn( new PersonTotalDataByFIO( SerDes
                        .getSerDes()
                        .getGetServiceErrorResponse()
                        .apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                : Mono.just( new PersonTotalDataByFIO( this.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "getCarTotalData" ) // возвращает данные по номеру машины
    public Mono< CarTotalData > getCarTotalData ( ApiResponseModel apiResponseModel ) {
        log.info( "Gos number: " + apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? Mono.just( new CarTotalData() )
                .flatMap( carTotalData -> {
                    SerDes
                            .getSerDes()
                            .getGetDoverennostList()
                            .apply( apiResponseModel.getStatus().getMessage() )
                            .subscribe( carTotalData::setDoverennostList );
                    SerDes
                            .getSerDes()
                            .getGetViolationList()
                            .apply( apiResponseModel.getStatus().getMessage() )
                            .subscribe( carTotalData::setViolationsList );
                    SerDes
                            .getSerDes()
                            .getGetVehicleTonirovka()
                            .apply( apiResponseModel.getStatus().getMessage() )
                            .subscribe( carTotalData::setTonirovka );
                    SerDes
                            .getSerDes()
                            .getGetVehicleData()
                            .apply( apiResponseModel.getStatus().getMessage() )
                            .subscribe( carTotalData::setModelForCar );

                    if ( carTotalData.getModelForCar() != null
                            && carTotalData.getModelForCar().getPinpp() != null
                            && !carTotalData.getModelForCar().getPinpp().isEmpty() )
                        carTotalData.setPsychologyCard( SerDes
                                .getSerDes()
                                .getPsychologyCard( ApiResponseModel
                                        .builder()
                                        .status( Status
                                                .builder()
                                                .message( carTotalData.getModelForCar().getPinpp() )
                                                .build() )
                                        .user( apiResponseModel.getUser() )
                                        .build() ) );
                    SerDes
                            .getSerDes()
                            .getInsurance()
                            .apply( apiResponseModel.getStatus().getMessage() )
                            .subscribe( carTotalData::setInsurance );
                    carTotalData.setCameraImage( apiResponseModel.getStatus().getMessage().split( "@$" )[0] );
                    carTotalData.setGosNumber( apiResponseModel.getStatus().getMessage().split( "@$" )[0] );
                    return Mono.just( carTotalData ); } )
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
                .mapNotNull( results ->
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
                                .getPsychologyCard( results, apiResponseModel )
                                : SerDes
                                .getSerDes()
                                .getPsychologyCard(
                                        new PsychologyCard( results ),
                                        token,
                                        apiResponseModel )
                                : new PsychologyCard( this.getErrorResponse.get() ) )
                : Mono.just( new PsychologyCard( this.getWrongParamResponse.get() ) ); }

    @MessageMapping ( value = "getPersonalCadastor" ) // возвращает данные по номеру кадастра
    public Flux< PsychologyCard > getPersonalCadastor ( ApiResponseModel apiResponseModel ) {
        log.info( apiResponseModel.getStatus().getMessage() );
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getDeserialize()
                .apply( apiResponseModel.getStatus().getMessage() )
                .map( Data::getPermanentRegistration )
                .flatMapMany( personList -> personList != null && !personList.isEmpty()
                        ? Flux.fromStream( personList.stream() )
                        .flatMap( person -> SerDes
                                .getSerDes()
                                .getGetModelForPassport()
                                .apply( person.getPPsp(),
                                        person.getPDateBirth() )
                                .map( data -> SerDes
                                        .getSerDes()
                                        .getPsychologyCard( data, apiResponseModel ) ) )
                        .onErrorContinue( ( error, object ) -> log.error( "Error: {} and reason: {}: ",
                                error.getMessage(), object ) )
                        .onErrorReturn( new PsychologyCard( SerDes
                                .getSerDes()
                                .getGetServiceErrorResponse()
                                .apply( "" ) ) )
                        : Flux.just( new PsychologyCard( SerDes
                        .getSerDes()
                        .getGetDataNotFoundErrorResponse()
                        .apply( apiResponseModel.getStatus().getMessage() ) ) ) )
                : Flux.just( new PsychologyCard( this.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "getPersonTotalDataByPinfl" ) // возвращает данные по Пинфл
    public Mono< PsychologyCard > getPersonTotalDataByPinfl ( ApiResponseModel apiResponseModel ) {
        return SerDes.getSerDes().getFlag()
                ? apiResponseModel.getStatus().getMessage() != null
                && apiResponseModel.getStatus().getMessage().length() > 0
                ? Mono.just( SerDes
                        .getSerDes()
                        .getPsychologyCard( apiResponseModel ) )
                .onErrorContinue( ( error, object ) -> log.error( "Error: {} and reason: {}: ",
                        error.getMessage(), object ) )
                .onErrorReturn( new PsychologyCard( SerDes.getSerDes().getGetServiceErrorResponse().apply( "" ) ) )
                : Mono.just( new PsychologyCard( this.getWrongParamResponse.get() ) )
                : Mono.just( new PsychologyCard( this.getErrorResponse.get() ) ); }

    @MessageMapping ( value = "getPersonDataByPassportSeriesAndBirthdate" ) // возвращает данные по номеру паспорта
    public Mono< PsychologyCard > getPersonDataByPassportSeriesAndBirthdate ( ApiResponseModel apiResponseModel ) {
        if ( apiResponseModel
                .getStatus()
                .getMessage() == null ) return Mono.just( new PsychologyCard( this.getWrongParamResponse.get() ) );
        String[] strings = apiResponseModel.getStatus().getMessage().split( "_" );
        return SerDes.getSerDes().getFlag()
                ? SerDes
                .getSerDes()
                .getGetModelForPassport()
                .apply( strings[ 0 ], strings[ 1 ] )
                .map( data -> SerDes
                        .getSerDes()
                        .getPsychologyCard( data, apiResponseModel ))
                .onErrorContinue( ( error, object ) -> log.error( "Error: {} and reason: {}: ",
                        error.getMessage(), object ) )
                .onErrorReturn( new PsychologyCard( SerDes
                        .getSerDes()
                        .getGetServiceErrorResponse()
                        .apply( Errors.SERVICE_WORK_ERROR.name() ) ) )
                : Mono.just( new PsychologyCard( this.getErrorResponse.get() ) ); }
}
