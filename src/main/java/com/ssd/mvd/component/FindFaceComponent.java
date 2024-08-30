package com.ssd.mvd.component;

import java.util.List;
import java.util.function.Function;

import com.ssd.mvd.entity.Results;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.FindFaceServiceApplication;
import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.inspectors.DataValidationInspector;

import reactor.core.publisher.Mono;
import org.springframework.messaging.rsocket.RSocketRequester;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public final class FindFaceComponent
        extends DataValidationInspector
        implements ServiceCommonMethods {
    private final RSocketRequester requester;
    private static FindFaceComponent component = new FindFaceComponent();

    @lombok.NonNull
    public static FindFaceComponent getInstance () {
        return component != null ? component : ( component = new FindFaceComponent() );
    }

    private FindFaceComponent () {
        this.requester = FindFaceServiceApplication.context.getBean( RSocketRequester.class );
    }

    private RSocketRequester getRequester() {
        return this.requester;
    }

    public final Function< String, Mono< Results > > getPapilonList = base64url -> this.getRequester()
            .route( Methods.GET_FACE_CARD.name() )
            .data( base64url )
            .retrieveMono( Results.class )
            .onErrorReturn( new Results() );

    public final Function< String, Mono< List > > getViolationListByPinfl = pinfl -> super.checkParam( pinfl )
            ? this.getRequester()
            .route( Methods.GET_VIOLATION_LIST_BY_PINFL.name() )
            .data( pinfl )
            .retrieveMono( List.class )
            .defaultIfEmpty( super.emptyList() )
            .onErrorReturn( super.emptyList() )
            : super.convert( super.emptyList() );

    @Override
    public void close() {
        this.getRequester().dispose();
        component = null;
        this.clean();
    }
}
