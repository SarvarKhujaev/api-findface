package com.ssd.mvd.publisher;

import org.apache.kafka.clients.producer.ProducerRecord;

import com.ssd.mvd.interfaces.KafkaCommonMethods;
import com.ssd.mvd.inspectors.CustomSerializer;

import org.reactivestreams.Subscription;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Publisher;

public final class CustomPublisher implements Publisher< ProducerRecord< String, String > > {
    private final ProducerRecord< String, String > producerRecord;

    public CustomPublisher(
            @lombok.NonNull final KafkaCommonMethods kafkaCommonMethods
    ) {
        this.producerRecord = new ProducerRecord<>( kafkaCommonMethods.getTopicName(), CustomSerializer.serialize( kafkaCommonMethods ) );
    }

    @Override
    public void subscribe( @lombok.NonNull final Subscriber subscriber ) {
        subscriber.onSubscribe( new Subscription() {
                @Override
                public void request( final long l ) {
                    subscriber.onNext( producerRecord );
                    subscriber.onComplete();
                }

                @Override
                public void cancel() {
                    subscriber.onError( new Exception( "Message was not sent!!!" ) );
                }
            } );
    }
}
