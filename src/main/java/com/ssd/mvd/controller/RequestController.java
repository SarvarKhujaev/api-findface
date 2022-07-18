package com.ssd.mvd.controller;

import com.ssd.mvd.entity.*;
import com.ssd.mvd.constants.Status;
import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.entity.modelForCadastr.Person;

import java.util.List;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.handler.annotation.MessageMapping;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping( value = "/findFaceService/api/v1/psychologyCard" )
public class RequestController {
    private final FindFaceComponent component;

    @MessageMapping ( value = "getPersonTotalData" )
    public Mono< PsychologyCard > getPersonTotalData ( String base64url ) { return base64url != null && base64url.length() > 0 ? this.component.getPapilonList( base64url )
            .filter( value -> value.getResults() != null && value.getResults().size() > 0 ).onErrorStop()
            .map( value -> value.getResults().get( 0 ).getCountry().equals( "УЗБЕКИСТАН" ) ? SerDes.getSerDes().getPsychologyCard( value.getResults().get( 0 ).getPassport().split( " " )[0], value.getResults(), value.getViolationList() )
                    : new PsychologyCard( value ) ).onErrorStop() : Mono.empty(); }

    @MessageMapping ( value = "getPersonTotalDataByPinfl" )
    public Mono< PsychologyCard > getPersonTotalDataByPinfl ( String pinfl ) { return Mono.just( SerDes.getSerDes().getPsychologyCard( pinfl, this.component ) ); }

    @MessageMapping ( value = "getPersonalCadastor" )
    public Flux< PsychologyCard > getPersonalCadastor ( String id ) {
        List< Person > personList = SerDes.getSerDes().deserialize( id ).getPermanentRegistration();
        if ( personList != null ) return Flux.fromStream( personList.stream() ).flatMap( person -> this.getPersonTotalData( person.getPCitizen() ) );
        else return Flux.empty(); }

    @MessageMapping ( value = "getCarTotalData" )
    public Mono< CarTotalData > getCarTotalData ( String platenumber ) { return Mono.just( new CarTotalData() ).flatMap( carTotalData -> {
        carTotalData.setDoverennostList( SerDes.getSerDes().getDoverennostList( platenumber ) );
        carTotalData.setViolationsList( SerDes.getSerDes().getViolationList( platenumber ) );
        carTotalData.setTonirovka( SerDes.getSerDes().getVehicleTonirovka( platenumber ) );
        carTotalData.setModelForCar( SerDes.getSerDes().getVehicleData( platenumber ) );
        carTotalData.setPsychologyCard( SerDes.getSerDes().getPsychologyCard( carTotalData.getModelForCar().getPinpp(), this.component ) );
        carTotalData.setInsurance( SerDes.getSerDes().insurance( platenumber ) );
        carTotalData.setCameraImage( platenumber.split( "@$" )[0] );
        carTotalData.setStatus( Status.CREATED );
        return Mono.just( carTotalData ); } ); }

//    @MessageMapping ( value = "linkPatrulToFindFaceCar" )
//    public Mono< ApiResponseModel > linkPatrulToFindFaceCar ( Request request ) { return Archive.getInstance().save( request ); }
//
//    @MessageMapping ( value = "addReportForFindFace" )
//    public Mono< ApiResponseModel > addReportForFindFace ( ReportForCard reportForCard ) { return Archive.getInstance().save( reportForCard ); }
}
