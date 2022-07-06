package com.ssd.mvd.controller;

import com.ssd.mvd.entity.*;
import com.ssd.mvd.database.Archive;
import com.ssd.mvd.constants.Status;
import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.database.CassandraDataControl;

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
    public Mono< PsychologyCard > getPersonTotalData ( String base64url ) { return this.component.getPapilonList( base64url )
            .log()
            .doOnError( System.out::println )
            .onErrorStop()
            .map( value -> SerDes.getSerDes().getPsychologyCard( value.getResults().get( 0 ).getPassport().split( " " )[0], value.getResults() ) ); }

    @MessageMapping ( value = "getCarTotalData" )
    public Mono< CarTotalData > getCarTotalData ( String platenumber ) { return Archive.getInstance().getPreferenceItemMapForCar().containsKey( platenumber ) ? Mono.just( Archive.getInstance().getCarTotalData( platenumber ) ) :
                Mono.just( new CarTotalData() ).flatMap( carTotalData -> {
                    carTotalData.setDoverennostList( SerDes.getSerDes().getDoverennostList( platenumber ) );
                    carTotalData.setViolationsList( SerDes.getSerDes().getViolationList( platenumber ) );
                    carTotalData.setTonirovka( SerDes.getSerDes().getVehicleTonirovka( platenumber ) );
                    carTotalData.setModelForCar( SerDes.getSerDes().getVehicleData( platenumber ) );
                    carTotalData.setPsychologyCard( SerDes.getSerDes().getPsychologyCsard( carTotalData.getModelForCar().getPinpp() )  );
                    carTotalData.setInsurance( SerDes.getSerDes().insurance( platenumber ) );
                    carTotalData.setCameraImage( platenumber.split( "@$" )[0] );
                    carTotalData.setStatus( Status.CREATED );
                    Archive.getInstance().save( CassandraDataControl.getInstance().addValue( carTotalData ) );
                    return Mono.just( carTotalData ); } ); }

    @MessageMapping ( value = "addReportForFindFace" )
    public Mono< ApiResponseModel > addReportForFindFace ( ReportForCard reportForCard ) { return Archive.getInstance().save( reportForCard ); }

    @MessageMapping ( value = "linkPatrulToFindFaceCar" )
    public Mono< ApiResponseModel > linkPatrulToFindFaceCar ( Request request ) { return Archive.getInstance().save( request ); }
}
