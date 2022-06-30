package com.ssd.mvd.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RSocketConfigs {
    @Lazy
    @Bean
    public WebClient webClient ( WebClient.Builder builder ) { return builder.build(); }

    @Lazy
    @Bean( name = "tablets" ) // connection to tablets service
    RSocketRequester tablets ( RSocketRequester.Builder builder ) { return builder.tcp( "10.254.1.229", 6060 ); }

    @Primary
    @Bean( name = "findFaceForImages" ) // connection to trackers service
    RSocketRequester connectToAssomidinService ( RSocketRequester.Builder builder ) { return builder.tcp( "10.254.1.2", 5055 ); }
}
