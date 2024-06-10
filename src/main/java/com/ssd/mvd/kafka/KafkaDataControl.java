package com.ssd.mvd.kafka;

import java.util.*;
import java.util.function.Supplier;
import java.util.function.Consumer;

import com.ssd.mvd.controller.Config;
import com.ssd.mvd.publisher.CustomPublisher;
import com.ssd.mvd.subscribers.CustomSubscriber;
import com.ssd.mvd.interfaces.ServiceCommonMethods;

import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import org.apache.kafka.clients.producer.ProducerConfig;

public final class KafkaDataControl extends Config implements ServiceCommonMethods {
    private final static KafkaDataControl instance = new KafkaDataControl();

    public static KafkaDataControl getInstance () {
        return instance;
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

    // записывает все ошибки в работе сервиса
    public final Consumer< String > writeErrorLog = errorLog -> this.kafkaSender
            .createOutbound()
            .send( new CustomPublisher( super.getERROR_LOGS(), errorLog ) )
            .then()
            .doOnError( super::logging )
            .doOnSuccess( success -> super.logging(
                    String.join(
                            " ",
                            "Kafka got error: ",
                            super.getERROR_LOGS(),
                            errorLog,
                            " at: ",
                            super.newDate().toString()
                    )
                )
            ).subscribe(
                    new CustomSubscriber<>(
                            success -> super.logging(
                                    String.join(
                                            " ",
                                            "Kafka got error: ",
                                            super.getERROR_LOGS(),
                                            errorLog,
                                            " at: ",
                                            super.newDate().toString()
                                    )
                            )
                    )
            );

    // записывает случае когда сервисы выдают ошибки
    public final Consumer< String > writeToKafkaErrorLog = errorLog -> this.kafkaSender
            .createOutbound()
            .send( new CustomPublisher( super.getADMIN_PANEL_ERROR_LOG(), errorLog ) )
            .then()
            .doOnError( super::logging )
            .doOnSuccess( success -> super.logging(
                    String.join(
                            " ",
                            "Kafka got error for : ",
                            super.getADMIN_PANEL_ERROR_LOG(),
                            errorLog,
                            " at: ",
                            super.newDate().toString()
                    )
                )
            ).subscribe(
                    new CustomSubscriber<>(
                            success -> super.logging(
                                    String.join(
                                            " ",
                                            "Kafka got error for : ",
                                            super.getADMIN_PANEL_ERROR_LOG(),
                                            errorLog,
                                            " at: ",
                                            super.newDate().toString()
                                    )
                            )
                    )
            );

    // регистрирует каждого оператора который запрашивает данные у сервиса
    public final Consumer< String > writeToKafkaServiceUsage = serviceUsage -> this.kafkaSender
            .createOutbound()
            .send( new CustomPublisher( super.getADMIN_PANEL(), serviceUsage ) )
            .then()
            .doOnError( super::logging )
            .doOnSuccess( success -> super.logging(
                    String.join(
                            " ",
                            "New user exposed your service: ",
                            super.getADMIN_PANEL(),
                            serviceUsage,
                            " at: ",
                            super.newDate().toString()
                    )
                )
            ).subscribe(
                    new CustomSubscriber<>(
                            success -> super.logging(
                                    String.join(
                                            " ",
                                            "New user exposed your service: ",
                                            super.getADMIN_PANEL(),
                                            serviceUsage,
                                            " at: ",
                                            super.newDate().toString()
                                    )
                            )
                    )
            );

    @Override
    public void close () {
        this.kafkaSender.close();
        super.logging( this );
    }
}
