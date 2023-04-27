package com.ssd.mvd.kafka;

import java.util.*;
import java.util.function.Supplier;
import java.util.function.Consumer;

import reactor.core.publisher.Mono;
import com.ssd.mvd.controller.Config;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

@lombok.Data
public class KafkaDataControl extends Config {
    private static KafkaDataControl instance = new KafkaDataControl();

    public static KafkaDataControl getInstance () { return instance != null ? instance : ( instance = new KafkaDataControl() ); }

    private final Supplier< Map< String, Object > > getKafkaSenderOptions = () -> Map.of(
            ProducerConfig.ACKS_CONFIG, "1",
            ProducerConfig.CLIENT_ID_CONFIG, super.getGROUP_ID_FOR_KAFKA(),
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, super.getKAFKA_BROKER(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class );

    private final KafkaSender< String, String > kafkaSender = KafkaSender.create(
            SenderOptions.< String, String >create( this.getGetKafkaSenderOptions().get() )
                    .maxInFlight( 1024 ) );

    private KafkaDataControl () { super.logging( "KafkaDataControl was created" ); }

    // записывает все ошибки в работе сервиса
    private final Consumer< String > writeErrorLog = errorLog -> this.getKafkaSender()
            .createOutbound()
            .send( Mono.just( new ProducerRecord<>( this.getERROR_LOGS(), errorLog ) ) )
            .then()
            .doOnError( error -> super.logging( error.getMessage() ) )
            .doOnSuccess( success -> super.logging( "Kafka got error: " + errorLog + " at: " + new Date() ) )
            .subscribe();

    // записывает случае когда сервисы выдают ошибки
    private final Consumer< String > writeToKafkaErrorLog = errorLog -> this.getKafkaSender()
            .createOutbound()
            .send( Mono.just( new ProducerRecord<>( this.getADMIN_PANEL_ERROR_LOG(), errorLog ) ) )
            .then()
            .doOnError( error -> super.logging( error.getMessage() ) )
            .doOnSuccess( success -> super.logging( "Kafka got error for ADMIN_PANEL_ERROR_LOG: " +
                    errorLog + " at: " + new Date() ) )
            .subscribe();

    // регистрирует каждого оператора который запрашивает данные у сервиса
    private final Consumer< String > writeToKafkaServiceUsage = serviceUsage -> this.getKafkaSender()
            .createOutbound()
            .send( Mono.just( new ProducerRecord<>( this.getADMIN_PANEL(), serviceUsage ) ) )
            .then()
            .doOnError( error -> super.logging( error.getMessage() ) )
            .doOnSuccess( success -> super.logging( "New user exposed your service: " + serviceUsage + " at: " + new Date() ) )
            .subscribe();
}
