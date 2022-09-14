package com.ssd.mvd.component;

import java.util.List;
import java.util.ArrayList;

import com.ssd.mvd.entity.Results;
import com.ssd.mvd.FindFaceServiceApplication;

import reactor.core.publisher.Mono;
import org.springframework.messaging.rsocket.RSocketRequester;

public class FindFaceComponent {
    private final RSocketRequester requester;
    private static FindFaceComponent component = new FindFaceComponent();

    public static FindFaceComponent getInstance () { return component != null ? component : ( component = new FindFaceComponent() ); }

    private FindFaceComponent () { this.requester = FindFaceServiceApplication.context.getBean( RSocketRequester.class ); }

    public Mono< Results > getPapilonList ( String base64url ) { return this.requester
            .route( "getFaceCard" )
            .data( base64url )
            .retrieveMono( Results.class ); }

    public Mono< List > getViolationListByPinfl ( String pinfl ) {
        try { return pinfl != null && !pinfl.isEmpty() ?
                this.requester
                        .route( "getViolationListByPinfl" )
                        .data( pinfl )
                        .retrieveMono( List.class )
                        .doOnError( throwable -> {
                            System.out.println( "ERROR: " + throwable.getCause() );
                            System.out.println( "ERROR: " + throwable.getMessage() ); } )
                        .defaultIfEmpty( new ArrayList() )
                : Mono.just( new ArrayList() );
        } catch ( Exception e ) { return Mono.empty(); } }

    public Mono< Results > getFamilyMembersData ( String pinfl ) {
        if ( pinfl != null && !pinfl.isEmpty() ) {
            System.out.println( "Pinfl in getFamilyMembersData: " + pinfl );
            return this.requester
                    .route( "getFamilyMembersData" )
                    .data( pinfl )
                    .retrieveMono( Results.class )
                    .defaultIfEmpty( new Results() ); }
        else return Mono.just( new Results() ); }
}
