package com.ssd.mvd.publisher;

import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
import com.ssd.mvd.controller.LogInspector;
import com.ssd.mvd.controller.SerDes;
import com.ssd.mvd.request.*;

import org.reactivestreams.Subscription;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Publisher;

public final class CustomPublisherForRequest extends LogInspector implements Publisher< String > {
    private final String value;

    public static CustomPublisherForRequest generate (
            final int integer, final Object object
    ) {
        return new CustomPublisherForRequest( integer, object );
    }

    private CustomPublisherForRequest ( final int integer, final Object object ) {
        this.value = SerDes
            .getSerDes()
            .getGson()
            .toJson( switch ( integer ) {
                case 1 -> RequestForCadaster.generate( String.valueOf( object ) );
                case 2 -> RequestForFio.generate( (FIO) object );
                case 3 -> RequestForModelOfAddress.generate( String.valueOf( object ) );
                case 4 -> RequestForBoardCrossing.generate( String.valueOf( object ) );
                default -> RequestForPassport.generate( String.valueOf( object ) );
            } );
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
