package com.ssd.mvd.component;

import java.util.List;
import java.util.Collections;
import java.util.function.Function;

import com.ssd.mvd.entity.Results;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.FindFaceServiceApplication;
import com.ssd.mvd.controller.DataValidationInspector;

import reactor.core.publisher.Mono;
import org.springframework.messaging.rsocket.RSocketRequester;

public final class FindFaceComponent extends DataValidationInspector {

    private final RSocketRequester requester;
    private static FindFaceComponent component = new FindFaceComponent();

    public static FindFaceComponent getInstance () { return component != null ? component : ( component = new FindFaceComponent() ); }

    private FindFaceComponent () { this.requester = FindFaceServiceApplication.context.getBean( RSocketRequester.class ); }

    public final Function< String, Mono< Results > > getPapilonList = base64url -> this.getRequester()
            .route( Methods.GET_FACE_CARD.name() )
            .data( base64url )
            .retrieveMono( Results.class )
            .onErrorReturn( new Results() );

    private RSocketRequester getRequester() { return this.requester; }

    public final Function< String, Mono< List > > getViolationListByPinfl = pinfl -> super.checkParam.test( pinfl )
            ? this.getRequester()
            .route( Methods.GET_VIOLATION_LIST_BY_PINFL.name() )
            .data( pinfl )
            .retrieveMono( List.class )
            .defaultIfEmpty( Collections.emptyList() )
            .onErrorReturn( Collections.emptyList() )
            : super.convert( Collections.emptyList() );
}
