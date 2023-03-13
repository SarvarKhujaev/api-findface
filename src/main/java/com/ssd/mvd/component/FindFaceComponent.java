package com.ssd.mvd.component;

import java.util.List;
import java.util.ArrayList;

import com.ssd.mvd.entity.Results;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.FindFaceServiceApplication;
import com.ssd.mvd.controller.DataValidationInspector;

import lombok.Data;
import reactor.core.publisher.Mono;
import org.springframework.messaging.rsocket.RSocketRequester;

@Data
public class FindFaceComponent {
    private final RSocketRequester requester;
    private static FindFaceComponent component = new FindFaceComponent();

    public static FindFaceComponent getInstance () { return component != null ? component : ( component = new FindFaceComponent() ); }

    private FindFaceComponent () { this.requester = FindFaceServiceApplication.context.getBean( RSocketRequester.class ); }

    public Mono< Results > getPapilonList ( String base64url ) {
        try { return this.getRequester()
                .route( Methods.GET_FACE_CARD.name() )
                .data( base64url )
                .retrieveMono( Results.class )
                .onErrorReturn( new Results() ); }
        catch ( Exception e ) { return Mono.just( new Results() ); } }

    public Mono< List > getViolationListByPinfl ( String pinfl ) {
        try { return DataValidationInspector
                .getInstance()
                .getCheckParam()
                .test( pinfl )
                ? this.getRequester()
                        .route( Methods.GET_VIOLATION_LIST_BY_PINFL.name() )
                        .data( pinfl )
                        .retrieveMono( List.class )
                        .defaultIfEmpty( new ArrayList() )
                        .onErrorReturn( new ArrayList() )
                : Mono.just( new ArrayList() ); }
        catch ( Exception e ) { return Mono.just( new ArrayList() ); } }
}
