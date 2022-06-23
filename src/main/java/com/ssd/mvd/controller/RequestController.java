package com.ssd.mvd.controller;

import com.ssd.mvd.database.Archive;
import com.ssd.mvd.entity.CarTotalData;
import com.ssd.mvd.entity.PsychologyCard;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class RequestController {
    @MessageMapping ( value = "getAllPsychologyCard" )
    public Flux< CarTotalData > getAll () { return Archive.getInstance().getAll(); }

    @MessageMapping( value = "getPsychologyCard" ) // fix the Pinfl data
    public Mono< PsychologyCard > getPsychologyCard ( String imageLink ) { return Archive.getInstance().save( imageLink ); }

    @MessageMapping ( value = "getCarTotalData" )
    public Mono< CarTotalData > getCarTotalData ( String id ) {
        CarTotalData carTotalData = Archive.getInstance().getCarTotalData( id );
        System.out.println( carTotalData );
        return Mono.just( carTotalData ); }
}
