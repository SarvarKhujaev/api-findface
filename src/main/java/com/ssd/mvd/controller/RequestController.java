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
    public Mono< PapilonList > getPersonTotalData ( String base64url ) { return this.component.getPapilonList( base64url ); }

    @MessageMapping ( value = "getCarTotalData" )
    public Mono< CarTotalData > getCarTotalData ( String platenumber ) { return Archive.getInstance().getPreferenceItemMapForCar().containsKey( platenumber ) ? Mono.just( Archive.getInstance().getCarTotalData( platenumber ) ) :
            Mono.just( Archive.getInstance().save( CassandraDataControl.getInstance().addValue( CarTotalData.builder()
                    .doverennostList( SerDes.getSerDes().getDoverennostList( platenumber ) )
                    .violationsList( SerDes.getSerDes().getViolationList( platenumber ) )
                    .tonirovka( SerDes.getSerDes().getVehicleTonirovka( platenumber ) )
                    .modelForCar( SerDes.getSerDes().getVehicleData( platenumber ) )
                    .insurance( SerDes.getSerDes().insurance( platenumber ) )
                    .cameraImage( platenumber.split( "@$" )[0] )
                    .status( Status.CREATED ).gosNumber( platenumber ).build() ) ) ); }

    @MessageMapping ( value = "addReportForFindFace" )
    public Mono< ApiResponseModel > addReportForFindFace ( ReportForCard reportForCard ) { return Archive.getInstance().save( reportForCard ); }

    @MessageMapping ( value = "linkPatrulToFindFaceCar" )
    public Mono< ApiResponseModel > linkPatrulToFindFaceCar ( Request request ) { return Archive.getInstance().save( request ); }
}
