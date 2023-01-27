package com.ssd.mvd.kafka;

import lombok.Data;
import java.util.*;
import java.util.logging.Logger;
import java.util.function.Supplier;
import java.util.function.Consumer;
import com.ssd.mvd.FindFaceServiceApplication;

import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

@Data
public class KafkaDataControl {
    private static KafkaDataControl instance = new KafkaDataControl();
    private final Logger logger = Logger.getLogger( KafkaDataControl.class.toString() );

    public static KafkaDataControl getInstance () { return instance != null ? instance : ( instance = new KafkaDataControl() ); }

    private final String KAFKA_BROKER = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.KAFKA_BROKER" );

    private final String GROUP_ID_FOR_KAFKA = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.GROUP_ID_FOR_KAFKA" );

    private final String ERROR_LOGS = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.ERROR_LOGS" );

    private final String ADMIN_PANEL = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.ADMIN_PANEL" );

    private final String ADMIN_PANEL_ERROR_LOG = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.ADMIN_PANEL_ERROR_LOG" );

    private final Supplier< Map< String, Object > > getKafkaSenderOptions = () -> Map.of(
            ProducerConfig.ACKS_CONFIG, "1",
            ProducerConfig.CLIENT_ID_CONFIG, this.getGROUP_ID_FOR_KAFKA(),
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.getKAFKA_BROKER(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class );

    private final KafkaSender< String, String > kafkaSender = KafkaSender.create(
            SenderOptions.< String, String >create( this.getGetKafkaSenderOptions().get() )
                    .maxInFlight( 1024 ) );

    private KafkaDataControl () { this.getLogger().info( "KafkaDataControl was created" ); }

    // записывает все ошибки в работе сервиса
    private final Consumer< String > writeErrorLog = errorLog -> this.getKafkaSender()
            .createOutbound()
            .send( Mono.just( new ProducerRecord<>( this.getERROR_LOGS(), errorLog ) ) )
            .then()
            .doOnError( error -> logger.info( error.getMessage() ) )
            .doOnSuccess( success -> logger.info( "Kafka got error: " +
                    errorLog + " at: " + new Date() ) )
            .subscribe();

    // записывает случае когда сервисы выдают ошибки
    private final Consumer< String > writeToKafkaErrorLog = errorLog -> this.getKafkaSender()
            .createOutbound()
            .send( Mono.just( new ProducerRecord<>( this.getADMIN_PANEL_ERROR_LOG(), errorLog ) ) )
            .then()
            .doOnError( error -> logger.info( error.getMessage() ) )
            .doOnSuccess( success -> logger.info( "Kafka got error for ADMIN_PANEL_ERROR_LOG: " +
                    errorLog + " at: " + new Date() ) )
            .subscribe();

    // регистрирует каждого оператора который запрашивает данные у сервиса
    private final Consumer< String > writeToKafkaServiceUsage = serviceUsage -> this.getKafkaSender()
            .createOutbound()
            .send( Mono.just( new ProducerRecord<>( this.getADMIN_PANEL(), serviceUsage ) ) )
            .then()
            .doOnError( error -> logger.info( error.getMessage() ) )
            .doOnSuccess( success -> logger.info( "New user exposed your service: "
                    + serviceUsage + " at: " + new Date() ) )
            .subscribe();
}
