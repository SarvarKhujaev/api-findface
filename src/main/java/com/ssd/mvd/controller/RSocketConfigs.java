package com.ssd.mvd.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;

@Configuration
public class RSocketConfigs {
    @Value( "${variables.LOAD_BALANCER}" )
    private String host;
    @Value( "${variables.PRODUCER_PORT}" )
    private Integer port;

    @Bean ( name = "findFaceForImage" )
    RSocketRequester tablets ( RSocketRequester.Builder builder ) {
        System.out.println( this.host + ":" + this.port );
        return builder.tcp( this.host, this.port ); }
}
