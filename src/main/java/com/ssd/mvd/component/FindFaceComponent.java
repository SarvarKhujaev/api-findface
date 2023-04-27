package com.ssd.mvd.component;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;

import com.ssd.mvd.entity.Results;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.FindFaceServiceApplication;

import reactor.core.publisher.Mono;
import org.springframework.messaging.rsocket.RSocketRequester;

@lombok.Data
public class FindFaceComponent {
    private final RSocketRequester requester;
    private static FindFaceComponent component = new FindFaceComponent();

    public static FindFaceComponent getInstance () { return component != null ? component : ( component = new FindFaceComponent() ); }

    private FindFaceComponent () { this.requester = FindFaceServiceApplication.context.getBean( RSocketRequester.class ); }

    private final Function< String, Mono< Results > > getPapilonList = base64url -> this.getRequester()
            .route( Methods.GET_FACE_CARD.name() )
            .data( base64url )
            .retrieveMono( Results.class )
            .onErrorReturn( new Results() );

    private final Function< String, Mono< List > > getViolationListByPinfl = pinfl -> pinfl != null
            && pinfl.length() > 1
            ? this.getRequester()
            .route( Methods.GET_VIOLATION_LIST_BY_PINFL.name() )
            .data( pinfl )
            .retrieveMono( List.class )
            .defaultIfEmpty( new ArrayList() )
            .onErrorReturn( new ArrayList() )
            : Mono.just( new ArrayList() );
}
