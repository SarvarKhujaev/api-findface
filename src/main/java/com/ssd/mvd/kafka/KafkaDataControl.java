package com.ssd.mvd.kafka;

import java.util.*;
import java.util.function.Supplier;

import com.ssd.mvd.inspectors.Config;
import com.ssd.mvd.publisher.CustomPublisher;
import com.ssd.mvd.subscribers.CustomSubscriber;
import com.ssd.mvd.interfaces.KafkaCommonMethods;
import com.ssd.mvd.interfaces.ServiceCommonMethods;

import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import org.apache.kafka.clients.producer.ProducerConfig;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public final class KafkaDataControl extends Config implements ServiceCommonMethods {
    private final static KafkaDataControl KAFKA_DATA_CONTROL = new KafkaDataControl();

    @lombok.NonNull
    public static KafkaDataControl getKafkaDataControl() {
        return KAFKA_DATA_CONTROL;
    }

    private final Supplier< Map< String, Object > > getKafkaSenderOptions = () -> Map.of(
            ProducerConfig.ACKS_CONFIG, super.getKAFKA_ACKS_CONFIG(),
            ProducerConfig.CLIENT_ID_CONFIG, super.getGROUP_ID_FOR_KAFKA(),
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, super.getKAFKA_BROKER(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class
    );

    private final KafkaSender< String, String > kafkaSender = KafkaSender.create(
            SenderOptions.< String, String >create( this.getKafkaSenderOptions.get() )
                    .maxInFlight( super.getKAFKA_SENDER_MAX_IN_FLIGHT() )
    );

    private KafkaDataControl () {
        super.logging( this.getClass() );
    }

    public void sendMessage (
            @lombok.NonNull final KafkaCommonMethods kafkaCommonMethods
    ) {
        this.kafkaSender
                .createOutbound()
                .send( new CustomPublisher( kafkaCommonMethods ) )
                .then()
                .doOnError( super::logging )
                .doOnSuccess( success -> super.logging( kafkaCommonMethods.getSuccessMessage() ) )
                .subscribe(
                        new CustomSubscriber<>( success -> super.logging( kafkaCommonMethods.getCompletedMessage() ) )
                );
    }

    @Override
    public void close () {
        this.kafkaSender.close();
        super.logging( this );
        this.clean();
    }
}
