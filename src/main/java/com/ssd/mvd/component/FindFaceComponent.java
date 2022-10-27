package com.ssd.mvd.component;

import java.util.List;
import java.util.ArrayList;

import com.ssd.mvd.entity.Results;
import com.ssd.mvd.FindFaceServiceApplication;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import reactor.core.publisher.Mono;
import io.rsocket.RSocketErrorException;
import org.springframework.messaging.rsocket.RSocketRequester;

@Data
@Slf4j
public class FindFaceComponent {
    private final RSocketRequester requester;
    private static FindFaceComponent component = new FindFaceComponent();

    public static FindFaceComponent getInstance () { return component != null ? component : ( component = new FindFaceComponent() ); }

    private FindFaceComponent () { this.requester = FindFaceServiceApplication.context.getBean( RSocketRequester.class ); }

    public Mono< Results > getPapilonList ( String base64url ) {
        try { return this.getRequester()
                .route( "getFaceCard" )
                .data( base64url )
                .retrieveMono( Results.class )
                .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                        error.getMessage(), object ) ) )
                .onErrorReturn( RSocketErrorException.class, new Results() ); }
        catch ( Exception e ) { return Mono.just( new Results() ); } }

    public Mono< Results > getFamilyMembersData ( String pinfl ) {
        try { return pinfl != null && !pinfl.isEmpty()
                ? this.getRequester()
                .route( "getFamilyMembersData" )
                .data( pinfl )
                .retrieveMono( Results.class )
                .defaultIfEmpty( new Results() )
                .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                        error.getMessage(), object ) ) )
                .onErrorReturn( new Results() )
                : Mono.just( new Results() );
        } catch ( Exception e ) { return Mono.just( new Results() ); } }

    public Mono< List > getViolationListByPinfl ( String pinfl ) {
        try { return pinfl != null && !pinfl.isEmpty() ?
                this.getRequester()
                        .route( "getViolationListByPinfl" )
                        .data( pinfl )
                        .retrieveMono( List.class )
                        .defaultIfEmpty( new ArrayList() )
                        .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                                error.getMessage(), object ) ) )
                        .onErrorReturn( new ArrayList() )
                : Mono.just( new ArrayList() ); }
        catch ( Exception e ) { return Mono.just( new ArrayList() ); } }
}
