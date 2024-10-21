package com.ssd.mvd.publisher;

import org.apache.kafka.clients.producer.ProducerRecord;
import com.ssd.mvd.interfaces.KafkaCommonMethods;

import org.reactivestreams.Subscription;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Publisher;

public final class CustomPublisher implements Publisher< ProducerRecord< String, byte[] > > {
    private final ProducerRecord< String, byte[] > producerRecord;

    public CustomPublisher(
            @lombok.NonNull final KafkaCommonMethods kafkaCommonMethods
    ) {
        this.producerRecord = new ProducerRecord<>(
                kafkaCommonMethods.getTopicName(),
                kafkaCommonMethods.getEntityRecord().toString().getBytes()
        );
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
