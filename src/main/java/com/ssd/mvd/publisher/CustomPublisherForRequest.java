package com.ssd.mvd.publisher;

import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
import com.ssd.mvd.controller.LogInspector;
import com.ssd.mvd.controller.SerDes;
import com.ssd.mvd.request.*;

import org.reactivestreams.Subscription;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Publisher;

public class CustomPublisherForRequest extends LogInspector implements Publisher< String > {
    private final String value;

    public CustomPublisherForRequest ( final Integer integer, final Object object ) {
        this.value = SerDes
            .getSerDes()
            .getGson()
            .toJson( switch ( integer ) {
                case 1 -> new RequestForCadaster( String.valueOf( object ) );
                case 2 -> new RequestForFio( (FIO) object );
                case 3 -> new RequestForModelOfAddress( String.valueOf( object ) );
                case 4 -> new RequestForBoardCrossing( String.valueOf( object ) );
                default -> new RequestForPassport( String.valueOf( object ) ); } ); }

    @Override
    public void subscribe( final Subscriber subscriber ) { subscriber.onSubscribe( new Subscription() {
        @Override
        public void request( final long l ) {
            subscriber.onNext( value );
            subscriber.onComplete(); }

        @Override
        public void cancel() { subscriber.onError( new Exception( "Message was not sent!!!" ) ); } } ); }
}
