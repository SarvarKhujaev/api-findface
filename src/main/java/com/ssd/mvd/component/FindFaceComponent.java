package com.ssd.mvd.component;

import java.util.List;
import com.ssd.mvd.entity.Results;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import com.ssd.mvd.FindFaceServiceApplication;
import org.springframework.stereotype.Component;
import org.springframework.messaging.rsocket.RSocketRequester;

@Component
@RequiredArgsConstructor
public class FindFaceComponent {
    private final RSocketRequester requester;

    private FindFaceComponent () { this.requester = FindFaceServiceApplication.context.getBean( "findFaceForImage", RSocketRequester.class ); }

    public Mono< Results > getPapilonList( String base64url ) { return this.requester.route( "getFaceCard" ).data( base64url ).retrieveMono( Results.class ); }

    public Mono< List > getViolationListByPinfl ( String pinfl ) { return pinfl != null ? this.requester.route( "getgetViolationListByPinfl" ).data( pinfl ).retrieveMono( List.class ) : null; }
}
