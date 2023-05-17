package com.ssd.mvd.publisher;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.reactivestreams.Subscription;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Publisher;

public final class CustomPublisher implements Publisher< ProducerRecord< String, String > > {
    private final ProducerRecord< String, String > producerRecord;

    public CustomPublisher( final String topic, final String message ) { this.producerRecord = new ProducerRecord<>( topic, message ); }

    @Override
    public void subscribe( final Subscriber subscriber ) { subscriber.onSubscribe( new Subscription() {
            @Override
            public void request( final long l ) {
                subscriber.onNext( producerRecord );
                subscriber.onComplete(); }

            @Override
            public void cancel() { subscriber.onError( new Exception( "Message was not sent!!!" ) ); } } ); }
}
