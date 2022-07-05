package com.ssd.mvd.controller;

import com.ssd.mvd.database.Archive;
import com.ssd.mvd.entity.PsychologyCard;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class RequestController {

    @MessageMapping( value = "getPsychologyCard" ) // fix the Pinfl data
    public Mono< PsychologyCard > getCard ( @PathVariable ( value = "imageLink" ) String imageLink ) { return Archive.getInstance().save( imageLink ); }
}
