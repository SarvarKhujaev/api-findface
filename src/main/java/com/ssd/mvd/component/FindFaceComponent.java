package com.ssd.mvd.component;

import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import com.ssd.mvd.entity.Results;

@Component
@RequiredArgsConstructor
public class FindFaceComponent {
    private final RSocketRequester requester;

    public Mono< Results > getPapilonList( String base64url ) { return this.requester.route( "getFaceCard" ).data( base64url ).retrieveMono( Results.class ); }
}
