package com.ssd.mvd.component;

import org.springframework.messaging.rsocket.RSocketRequester;
import com.ssd.mvd.entity.modelForFindFace.PreferenceItem;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class FindFaceComponent {
    private final RSocketRequester requester;

    public Flux< PreferenceItem > getPreferenceItem ( String id ) { return this.requester.route( "getCarById" ).data( id ).retrieveFlux( PreferenceItem.class ); }
}
