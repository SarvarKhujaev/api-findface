package com.ssd.mvd.controller;

import com.ssd.mvd.entity.Request;
import com.ssd.mvd.database.Archive;
import com.ssd.mvd.entity.CarTotalData;
import com.ssd.mvd.entity.ApiResponseModel;
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

//    @MessageMapping( value = "getPsychologyCard" ) // fix the Pinfl data
//    public Mono< PsychologyCard > getPsychologyCard ( String imageLink ) { return Archive.getInstance().save( imageLink ); }

    @MessageMapping ( value = "getCarTotalData" )
    public Mono< CarTotalData > getCarTotalData ( String plateNumber ) { return Archive.getInstance().getPreferenceItemMapForCar().containsKey( plateNumber ) ? Mono.just( Archive.getInstance().getCarTotalData( plateNumber ) ) :
            Mono.just( Archive.getInstance().save( CassandraDataControl.getInstance().addValue( CarTotalData.builder()
            .gosNumber( plateNumber )
            .modelForCar( SerDes.getSerDes().getVehicleData( plateNumber ) )
            .tonirovka( SerDes.getSerDes().getVehicleTonirovka( plateNumber ) )
            .violationsList( SerDes.getSerDes().getViolationList( plateNumber ) )
            .doverennostList( SerDes.getSerDes().getDoverennostList( plateNumber ) ).build() ) ) ); }

    @MessageMapping ( value = "linkPatrulToFindFaceCar" )
    public Mono< ApiResponseModel > linkPatrulToFindFaceCar ( Request request ) { return Mono.just( ApiResponseModel.builder().build() ); }
}
