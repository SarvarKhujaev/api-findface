package com.ssd.mvd.controller;

import com.ssd.mvd.entity.ReportForCard;
import com.ssd.mvd.entity.Request;
import com.ssd.mvd.database.Archive;
import com.ssd.mvd.constants.Status;
import com.ssd.mvd.entity.CarTotalData;
import com.ssd.mvd.entity.ApiResponseModel;
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
    @MessageMapping ( value = "getCarTotalData" )
    public Mono< CarTotalData > getCarTotalData ( String plateNumber ) { return Archive.getInstance().getPreferenceItemMapForCar().containsKey( plateNumber ) ? Mono.just( Archive.getInstance().getCarTotalData( plateNumber ) ) :
            Mono.just( Archive.getInstance().save( CassandraDataControl.getInstance().addValue( CarTotalData.builder()
                    .doverennostList( SerDes.getSerDes().getDoverennostList( plateNumber ) )
                    .violationsList( SerDes.getSerDes().getViolationList( plateNumber ) )
                    .tonirovka( SerDes.getSerDes().getVehicleTonirovka( plateNumber ) )
                    .modelForCar( SerDes.getSerDes().getVehicleData( plateNumber ) )
                    .status( Status.CREATED ).gosNumber( plateNumber ).build() ) ) ); }

    @MessageMapping ( value = "addReportForFindFace" )
    public Mono< ApiResponseModel > addReportForFindFace ( ReportForCard reportForCard ) { return Archive.getInstance().save( reportForCard ); }

    @MessageMapping ( value = "linkPatrulToFindFaceCar" )
    public Mono< ApiResponseModel > linkPatrulToFindFaceCar ( Request request ) { return Archive.getInstance().save( request ); }
}
