package com.ssd.mvd.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.ssd.mvd.entity.*;
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

    @MessageMapping ( value = "ping" )
    public Mono< Boolean > ping () { return Mono.just( true ); }

    @MessageMapping ( value = "getFamilyMembersData" )
    public Mono< Results > getFamilyMembersData ( String pinfl ) { return FindFaceComponent
            .getInstance()
            .getFamilyMembersData( pinfl )
            .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                    error.getMessage(), object ) ) )
            .onErrorReturn( new Results() ); }

    @MessageMapping ( value = "getPersonTotalDataByFIO" ) // возвращает данные по ФИО человека
    public Mono< PersonTotalDataByFIO > getPersonTotalDataByFIO ( FIO fio ) { return SerDes
            .getSerDes()
            .getPersonTotalDataByFIO ( fio )
            .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                    error.getMessage(), object ) ) )
            .onErrorReturn( new PersonTotalDataByFIO() ); }

    @MessageMapping ( value = "getCarTotalData" ) // возвращает данные по номеру машины
    public Mono< CarTotalData > getCarTotalData ( ApiResponseModel apiResponseModel ) {
        log.info( "Gos number: " + apiResponseModel.getStatus().getMessage() );
        return Mono.just( new CarTotalData() )
                .flatMap( carTotalData -> {
                    carTotalData.setDoverennostList( SerDes
                            .getSerDes()
                            .getGetDoverennostList()
                            .apply( apiResponseModel.getStatus().getMessage() ) );
                    carTotalData.setViolationsList( SerDes
                            .getSerDes()
                            .getGetViolationList()
                            .apply( apiResponseModel.getStatus().getMessage() ) );
                    carTotalData.setTonirovka( SerDes
                            .getSerDes()
                            .getGetVehicleTonirovka()
                            .apply( apiResponseModel.getStatus().getMessage() ) );
                    carTotalData.setModelForCar( SerDes
                            .getSerDes()
                            .getGetVehicleData()
                            .apply( apiResponseModel.getStatus().getMessage() ) );
                    if ( carTotalData.getModelForCar() != null
                            && carTotalData.getModelForCar().getPinpp() != null )
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
                    carTotalData.setInsurance( SerDes
                            .getSerDes()
                            .getInsurance()
                            .apply( apiResponseModel.getStatus().getMessage() ) );
                    carTotalData.setCameraImage( apiResponseModel.getStatus().getMessage().split( "@$" )[0] );
                    carTotalData.setGosNumber( apiResponseModel.getStatus().getMessage().split( "@$" )[0] );
                    return Mono.just( carTotalData ); } )
                .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                        error.getMessage(), object ) ) )
                .onErrorReturn( new CarTotalData() ); }

    @MessageMapping ( value = "getPersonTotalData" ) // возвращает данные по фотографии
    public Mono< PsychologyCard > getPersonTotalData ( ApiResponseModel apiResponseModel ) {
        String base64url = apiResponseModel.getStatus().getMessage();
        token = base64url.split( "@" )[ 1 ];
        base64url = base64url.split( "@" )[ 0 ];
        return base64url != null && base64url.length() > 0 ?
                FindFaceComponent
                        .getInstance()
                        .getPapilonList( base64url )
                        .filter( value -> value.getResults() != null
                                && value.getResults().size() > 0 )
                        .mapNotNull( results -> results
                                .getResults()
                                .get( 0 )
                                .getCountry()
                                .equals( "УЗБЕКИСТАН" ) ?
                                SerDes
                                .getSerDes()
                                .getPsychologyCard( results, apiResponseModel )
                                : SerDes
                                .getSerDes()
                                .getPsychologyCard( new PsychologyCard( results ), token, apiResponseModel ) )
                : Mono.just( new PsychologyCard() ); }

    @MessageMapping ( value = "getPersonalCadastor" ) // возвращает данные по номеру кадастра
    public Flux< PsychologyCard > getPersonalCadastor ( ApiResponseModel apiResponseModel ) {
        log.info( apiResponseModel.getStatus().getMessage() );
        List< Person > personList = SerDes
                .getSerDes()
                .getDeserialize()
                .apply( apiResponseModel.getStatus().getMessage() )
                .getPermanentRegistration();
        return personList != null
                && !personList.isEmpty() ?
                Flux.fromStream( personList.stream() )
                    .flatMap( person -> Mono.just( SerDes
                            .getSerDes()
                            .getPsychologyCard( SerDes
                                    .getSerDes()
                                    .deserialize( person.getPPsp(), person.getPDateBirth() ),
                                    apiResponseModel ) ) )
                    .onErrorContinue( (error, object) -> log.error( "Error: {} and reason: {}: ",
                            error.getMessage(), object ) )
                    .onErrorReturn( new PsychologyCard() )
                    : Flux.just( new PsychologyCard() ); }

    @MessageMapping ( value = "getTestPersonalCadastor" ) // возвращает данные по Пинфл
    public Flux< PsychologyCard > getTestPersonTotalDataByPinfl ( ApiResponseModel apiResponseModel ) {
        System.out.println( apiResponseModel.getStatus().getMessage() );
        return Flux.range( 1, 5 )
                .flatMap(integer -> Mono.just( new PsychologyCard() ) ); }

    @MessageMapping ( value = "getPersonTotalDataByPinfl" ) // возвращает данные по Пинфл
    public Mono< PsychologyCard > getPersonTotalDataByPinfl ( ApiResponseModel apiResponseModel ) {
        return apiResponseModel.getStatus().getMessage() != null
                && apiResponseModel.getStatus().getMessage().length() > 0 ?
                Mono.just( SerDes
                        .getSerDes()
                        .getPsychologyCard( apiResponseModel ) )
                : Mono.just( new PsychologyCard() ); }

    @MessageMapping ( value = "getPersonDataByPassportSeriesAndBirthdate" ) // возвращает данные по номеру паспорта
    public Mono< PsychologyCard > getPersonDataByPassportSeriesAndBirthdate ( ApiResponseModel apiResponseModel ) {
        String[] strings = apiResponseModel.getStatus().getMessage().split( "_" );
        return Mono.just( SerDes
                .getSerDes()
                .getPsychologyCard( SerDes
                        .getSerDes()
                        .deserialize( strings[ 0 ], strings[ 1 ] ), apiResponseModel ) )
                .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                        error.getMessage(), object ) ) )
                .onErrorReturn( new PsychologyCard() ); }
}
