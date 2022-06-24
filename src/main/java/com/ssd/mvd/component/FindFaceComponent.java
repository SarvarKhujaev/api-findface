package com.ssd.mvd.component;

import com.ssd.mvd.entity.modelForFindFace.PreferenceItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FindFaceComponent {
    private final RSocketRequester requester;

    public Mono< PreferenceItem > getPreferenceItem ( String id ) { return this.requester.route( "getCardById" ).data( id ).retrieveMono( PreferenceItem.class ); }
}
