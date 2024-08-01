package com.ssd.mvd.publisher;

import com.ssd.mvd.interfaces.RequestCommonMethods;
import com.ssd.mvd.inspectors.CustomSerializer;

import org.reactivestreams.Subscription;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Publisher;

public final class CustomPublisherForRequest extends CustomSerializer implements Publisher< String > {
    private final String value;

    public static < T, U > CustomPublisherForRequest generate (
            final U object,
            final RequestCommonMethods< T, U > request
    ) {
        return new CustomPublisherForRequest( object, request );
    }

    private <T, U> CustomPublisherForRequest (
            final U object,
            final RequestCommonMethods< T, U > request
    ) {
        this.value = super.serialize( request.generate( object ) );
    }

    @Override
    public void subscribe( final Subscriber subscriber ) {
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
