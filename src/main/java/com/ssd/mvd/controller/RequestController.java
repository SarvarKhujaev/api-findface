package com.ssd.mvd.controller;

import com.ssd.mvd.entity.*;
import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.entity.modelForCadastr.Person;
import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
import com.ssd.mvd.entity.modelForFioOfPerson.PersonTotalDataByFIO;

import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.handler.annotation.MessageMapping;

@RestController
public class RequestController {
    @MessageMapping ( value = "getPersonTotalData" )
    public Mono< PsychologyCard > getPersonTotalData ( String base64url ) { return base64url != null && base64url.length() > 0 ?
            FindFaceComponent
                    .getInstance()
                    .getPapilonList( base64url )
                    .filter( value -> value.getResults() != null && value.getResults().size() > 0 )
                    .onErrorStop()
                    .map( results -> results
                            .getResults()
                            .get( 0 )
                            .getCountry()
                            .equals( "УЗБЕКИСТАН" ) ?
                            SerDes
                                    .getSerDes()
                                    .getPsychologyCard( results
                                                    .getResults()
                                                    .get( 0 )
                                                    .getPassport()
                                                    .split( " " )[0], results )
                            : new PsychologyCard( results ) )
                            .onErrorStop() : Mono.just( new PsychologyCard() ); }

    @MessageMapping ( value = "getPersonTotalDataByPinfl" )
    public Mono< PsychologyCard > getPersonTotalDataByPinfl ( String pinfl ) { return Mono.just( SerDes
            .getSerDes()
            .getPsychologyCard( pinfl ) ); }

    @MessageMapping ( value = "getPersonalCadastor" )
    public Flux< PsychologyCard > getPersonalCadastor ( String id ) {
        List< Person > personList = SerDes.getSerDes().deserialize( id ).getPermanentRegistration();
        return personList != null ? Flux.fromStream( personList.stream() )
                .flatMap( person -> Mono.just( SerDes
                        .getSerDes()
                        .getPsychologyCard( SerDes
                            .getSerDes()
                            .deserialize( person.getPPsp(), person.getPDateBirth() ) ) ) ) : Flux.empty(); }

    @MessageMapping ( value = "getCarTotalData" )
    public Mono< CarTotalData > getCarTotalData ( String platenumber ) {
        System.out.println( "Gos number: " + platenumber );
        return Mono.just( new CarTotalData() )
            .flatMap( carTotalData -> {
//        carTotalData.setDoverennostList( SerDes.getSerDes().getDoverennostList( platenumber ) );
        carTotalData.setViolationsList( SerDes.getSerDes().getViolationList( platenumber ) );
        carTotalData.setTonirovka( SerDes.getSerDes().getVehicleTonirovka( platenumber ) );
        carTotalData.setModelForCar( SerDes.getSerDes().getVehicleData( platenumber ) );
        carTotalData.setPsychologyCard( SerDes.getSerDes()
                .getPsychologyCard( carTotalData.getModelForCar().getPinpp() ) );
        carTotalData.setInsurance( SerDes.getSerDes().insurance( platenumber ) );
        carTotalData.setCameraImage( platenumber.split( "@$" )[0] );
        carTotalData.setGosNumber( platenumber.split( "@$" )[0] );
        return Mono.just( carTotalData ); } ); }

    @MessageMapping ( value = "getPersonDataByPassportSeriesAndBirthdate" )
    public Mono< PsychologyCard > getPersonDataByPassportSeriesAndBirthdate ( String data ) {
        String[] strings = data.split( "_" );
        return Mono.just( SerDes
                .getSerDes()
                .getPsychologyCard( SerDes
                        .getSerDes()
                        .deserialize( strings[ 0 ], strings[ 1 ] ) ) ); }

    @MessageMapping ( value = "getPersonTotalDataByFIO" )
    public Mono< PersonTotalDataByFIO > getPersonTotalDataByFIO ( FIO fio ) { return SerDes
            .getSerDes()
            .getPersonTotalDataByFIO ( fio ); }

    @MessageMapping ( value = "getFamilyMembersData" )
    public Mono< Results > getFamilyMembersData ( String pinfl ) { return FindFaceComponent
            .getInstance()
            .getFamilyMembersData( pinfl ); }
}
