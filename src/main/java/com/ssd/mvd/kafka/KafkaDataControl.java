package com.ssd.mvd.kafka;

import java.util.*;
import java.util.function.Supplier;

import com.ssd.mvd.inspectors.Config;
import com.ssd.mvd.publisher.CustomPublisher;
import com.ssd.mvd.subscribers.CustomSubscriber;
import com.ssd.mvd.interfaces.KafkaCommonMethods;

import reactor.kafka.sender.KafkaSender;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.sender.SenderOptions;

import org.apache.kafka.common.record.CompressionType;
import org.apache.kafka.clients.producer.ProducerConfig;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public final class KafkaDataControl extends Config {
    private final static KafkaDataControl KAFKA_DATA_CONTROL = new KafkaDataControl();

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized KafkaDataControl getKafkaDataControl() {
        return KAFKA_DATA_CONTROL;
    }

    private final Supplier< WeakHashMap< String, Object > > getKafkaSenderOptions = () -> {
        final WeakHashMap< String, Object > options = newMap();

        options.put( ProducerConfig.ACKS_CONFIG, getKAFKA_ACKS_CONFIG() );

        // The number of times to retry sending a message if it fails.
        options.put( ProducerConfig.RETRIES_CONFIG, getRETRIES_CONFIG() );

        // The maximum time to wait before sending a batch to the broker
        options.put( ProducerConfig.LINGER_MS_CONFIG, getLINGER_MS_CONFIG() );

        // The maximum size of the batch to send to the broker
        options.put( ProducerConfig.BATCH_SIZE_CONFIG, getBATCH_SIZE_CONFIG() );

        options.put( ProducerConfig.CLIENT_ID_CONFIG, getGROUP_ID_FOR_KAFKA() );

        // The maximum amount of memory to use for buffering messages
        options.put( ProducerConfig.BUFFER_MEMORY_CONFIG, getBUFFER_MEMORY_CONFIG() );

        // The maximum time to wait for a response from the broker
        options.put( ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, getREQUEST_TIMEOUT_MS_CONFIG() );

        // The maximum number of outstanding requests to send to the broker
        options.put( ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, getKAFKA_SENDER_MAX_IN_FLIGHT() );

        // The compression algorithm to use for messages
        options.put(
                ProducerConfig.COMPRESSION_TYPE_CONFIG,
                checkContextOrReturnDefaultValue(
                        "variables.KAFKA_VARIABLES.COMPRESSION_TYPE_CONFIG",
                        CompressionType.LZ4.name
                )
        );

        options.put( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getKAFKA_BROKER() );

        // The maximum age of metadata in milliseconds
        options.put( ProducerConfig.METADATA_MAX_AGE_CONFIG, getMETADATA_MAX_AGE_CONFIG() );

        options.put( ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, KafkaProducerInterceptor.class.getName() );

        options.put( ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class.getName() );
        options.put( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.ByteArraySerializer.class.getName() );

        return options;
    };

    private final KafkaSender< String, byte[] > kafkaSender = KafkaSender.create(
            SenderOptions.< String, byte[] >create( this.getKafkaSenderOptions.get() )
                    .scheduler( Schedulers.parallel() )
                    .maxInFlight( getKAFKA_SENDER_MAX_IN_FLIGHT() )
                    .withKeySerializer( new org.apache.kafka.common.serialization.StringSerializer() )
                    .withValueSerializer( new org.apache.kafka.common.serialization.ByteArraySerializer() )
    );

    private KafkaDataControl () {
        super( KafkaDataControl.class );
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
