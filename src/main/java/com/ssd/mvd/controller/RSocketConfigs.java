package com.ssd.mvd.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;

@Configuration
public class RSocketConfigs {
    @Value( "${variables.RSOCKET_VARIABLES.LOAD_BALANCER}" )
    private String host;
    @Value( "${variables.RSOCKET_VARIABLES.PRODUCER_PORT}" )
    private Integer port;

    @Bean
    RSocketRequester findFace ( RSocketRequester.Builder builder ) { return builder.tcp( this.host, this.port ); }
}
