package com.ssd.mvd.component;

import java.util.List;
import com.ssd.mvd.entity.Results;
import reactor.core.publisher.Mono;
import com.ssd.mvd.FindFaceServiceApplication;
import org.springframework.messaging.rsocket.RSocketRequester;

public class FindFaceComponent {
    private final RSocketRequester requester;
    private static FindFaceComponent component = new FindFaceComponent();

    public static FindFaceComponent getInstance () { return component != null ? component : ( component = new FindFaceComponent() ); }

    private FindFaceComponent () { this.requester = FindFaceServiceApplication.context.getBean( RSocketRequester.class ); }

    public Mono< Results > getPapilonList ( byte[] file ) { return this.requester.route( "getWithFile" ).data( file ).retrieveMono( Results.class ); }

    public Mono< Results > getPapilonList( String base64url ) { return this.requester.route( "getFaceCard" ).data( base64url ).retrieveMono( Results.class ); }

    public Mono< List > getViolationListByPinfl ( String pinfl ) { return pinfl != null ?
            this.requester.route( "getViolationListByPinfl" )
            .data( pinfl ).retrieveMono( List.class ) : null; }
}
