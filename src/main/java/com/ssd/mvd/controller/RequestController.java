package com.ssd.mvd.controller;

import java.util.List;
import java.time.Duration;
import java.util.ArrayList;

import com.ssd.mvd.entity.*;
import com.ssd.mvd.database.Archive;
import com.ssd.mvd.constants.Status;
import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.database.CassandraDataControl;

import com.ssd.mvd.entity.modelForCadastr.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.handler.annotation.MessageMapping;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping( value = "/findFaceService/api/v1/" )
public class RequestController {
    private final FindFaceComponent component;

    @MessageMapping ( value = "getPersonTotalData" )
    public Mono< PsychologyCard > getPersonTotalData ( String base64url ) {
        System.out.println( "Person Data" );
        return base64url != null && base64url.length() > 0 ? this.component.getPapilonList( base64url )
            .filter( value -> value.getResults() != null && value.getResults().size() > 0 )
            .onErrorStop()
            .map( value -> value.getResults().get( 0 ).getCountry().equals( "УЗБЕКИСТАН" ) ? SerDes.getSerDes().getPsychologyCard( value.getResults().get( 0 ).getPassport().split( " " )[0], value.getResults(), value.getViolationList() )
                    : new PsychologyCard( value ) ).onErrorStop() : Mono.empty(); }

    @MessageMapping ( value = "getPersonalCadastor" )
    public Mono< List< PsychologyCard > > getPersonalCadastor ( String id ) {
        System.out.println( "Cadastor" );
        List< PsychologyCard > list = new ArrayList<>();
        List< Person > personList = SerDes.getSerDes().deserialize( id ).getPermanentRegistration();
        if ( personList != null && personList.size() > 0 ) Flux.fromStream( personList.stream() ).map( person -> this.getPersonTotalData( SerDes.getSerDes().getImageByPnfl( person.getPCitizen() ) ).subscribe( psychologyCard -> list.add( psychologyCard ) ) );
        return Mono.just( list ).delayElement( Duration.ofMillis( list.size() > 0 ? 300L * list.size() : 0L ) ); }

    @MessageMapping ( value = "getCarTotalData" )
    public Mono< CarTotalData > getCarTotalData ( String platenumber ) {
        System.out.println( "Person" );
        return Archive.getInstance().getPreferenceItemMapForCar().containsKey( platenumber ) ? Mono.just( Archive.getInstance().getCarTotalData( platenumber ) ) :
                Mono.just( new CarTotalData() ).flatMap( carTotalData -> {
                    carTotalData.setDoverennostList( SerDes.getSerDes().getDoverennostList( platenumber ) );
                    carTotalData.setViolationsList( SerDes.getSerDes().getViolationList( platenumber ) );
                    carTotalData.setTonirovka( SerDes.getSerDes().getVehicleTonirovka( platenumber ) );
                    carTotalData.setModelForCar( SerDes.getSerDes().getVehicleData( platenumber ) );
                    carTotalData.setPsychologyCard( SerDes.getSerDes().getPsychologyCard( carTotalData.getModelForCar().getPinpp() ) );
                    carTotalData.setInsurance( SerDes.getSerDes().insurance( platenumber ) );
                    carTotalData.setCameraImage( platenumber.split( "@$" )[0] );
                    carTotalData.setStatus( Status.CREATED );
                    Archive.getInstance().save( CassandraDataControl.getInstance().addValue( carTotalData ) );
                    return Mono.just( carTotalData ); } ); }

    @MessageMapping ( value = "linkPatrulToFindFaceCar" )
    public Mono< ApiResponseModel > linkPatrulToFindFaceCar ( Request request ) { return Archive.getInstance().save( request ); }

    @MessageMapping ( value = "addReportForFindFace" )
    public Mono< ApiResponseModel > addReportForFindFace ( ReportForCard reportForCard ) { return Archive.getInstance().save( reportForCard ); }
}
