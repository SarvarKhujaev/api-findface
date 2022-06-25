package com.ssd.mvd.controller;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;

import com.ssd.mvd.entity.Request;
import com.ssd.mvd.entity.CarTotalData;
import com.ssd.mvd.entity.ApiResponseModel;
import com.ssd.mvd.component.FindFaceComponent;

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
    public Flux< CarTotalData > getCarTotalData ( String id ) { return component.getPreferenceItem( id ).flatMap( preferenceItem -> Mono.just( CarTotalData.builder().cameraImage( preferenceItem.getFullframe() )
            .doverennostList( SerDes.getSerDes().getDoverennostList( preferenceItem.getFeatures().getLicense_plate_number().getName() ) )
            .violationsList( SerDes.getSerDes().getViolationList( preferenceItem.getFeatures().getLicense_plate_number().getName() ) )
            .tonirovka( SerDes.getSerDes().getVehicleTonirovka( preferenceItem.getFeatures().getLicense_plate_number().getName() ) )
            .modelForCar( SerDes.getSerDes().getVehicleData( preferenceItem.getFeatures().getLicense_plate_number().getName() ) )
            .gosNumber( preferenceItem.getFeatures().getLicense_plate_number().getName() )
            .confidence( preferenceItem.getFeatures().getModel().getConfidence() )
            .id ( preferenceItem.getDetector_params().getTrack().getId() )
            .brand( preferenceItem.getFeatures().getModel().getName() )
            .color( preferenceItem.getFeatures().getColor().getName() )
            .type( preferenceItem.getFeatures().getMake().getName() )
            .matched( preferenceItem.getMatched() ).build() ) ); }

    @MessageMapping ( value = "linkPatrulToFindFaceCar" )
    public Mono< ApiResponseModel > linkPatrulToFindFaceCar ( Request request ) { return Mono.just( ApiResponseModel.builder().build() ); }
}
