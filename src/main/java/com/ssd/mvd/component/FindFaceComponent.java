package com.ssd.mvd.component;

import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import com.ssd.mvd.entity.PapilonList;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FindFaceComponent {
    private final RSocketRequester requester;

    public Mono< PapilonList > getPapilonList( String base64url ) { return this.requester.route( "getFaceCard" ).data( base64url ).retrieveMono( PapilonList.class ); }

//    public Flux< PreferenceItem > getPreferenceItem ( String id ) { return this.requester.route( "getCarById" ).data( id ).retrieveFlux( PreferenceItem.class ); }
}
