package com.ssd.mvd.publisher;

import com.ssd.mvd.interfaces.RequestCommonMethods;
import com.ssd.mvd.inspectors.CustomSerializer;

import org.reactivestreams.Subscription;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Publisher;

public final class CustomPublisherForRequest extends CustomSerializer implements Publisher< String > {
    private final String value;

    public <T, U> CustomPublisherForRequest (
            @lombok.NonNull final U object,
            @lombok.NonNull final RequestCommonMethods< T, U > request
    ) {
        this.value = super.serialize( request.generate( object ) );
    }

    @Override
    public void subscribe( @lombok.NonNull final Subscriber subscriber ) {
        subscriber.onSubscribe( new Subscription() {
                @Override
                public void request( final long l ) {
                    subscriber.onNext( value );
                    subscriber.onComplete();
                }

                @Override
                public void cancel() {
                    subscriber.onError( new Exception( "Message was not sent!!!" ) );
                }
        } );
    }
}
