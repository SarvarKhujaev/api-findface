package com.ssd.mvd.database;

import com.ssd.mvd.entity.CarTotalData;
import com.ssd.mvd.entity.PsychologyCard;

import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.apache.kafka.clients.admin.AdminClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.Collections;
import java.time.LocalDateTime;
import java.util.logging.Logger;
import java.util.concurrent.ExecutionException;

public class KafkaDataControl {
    private Properties properties;
    private final AdminClient client;
    public final String ID = "SSD.FindFace";
    private final KafkaTemplate< String, String > kafkaTemplate;
    private static KafkaDataControl instance = new KafkaDataControl();
    private final Logger logger = Logger.getLogger( KafkaDataControl.class.toString() );
    public final String PATH = "10.254.1.209:9092, 10.254.1.211:9092, 10.254.1.212:9092";

    private Properties setProperties () {
        this.properties = new Properties();
        this.properties.put( AdminClientConfig.CLIENT_ID_CONFIG, this.ID );
        this.properties.put( AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, this.PATH );
        return properties; }

    private void getNewTopic ( String imei ) {
        this.client.createTopics( Collections.singletonList( TopicBuilder.name( imei ).partitions(5 ).replicas(3 ).build() ) );
        this.logger.info( "Topic: " + imei + " was created" );
        new Thread( new KafkaConsumer( imei ), imei ).start(); }

    public static KafkaDataControl getInstance () { return instance != null ? instance : ( instance = new KafkaDataControl() ); }

    private KafkaDataControl() {
        this.client = KafkaAdminClient.create( this.setProperties() );
        this.logger.info( "KafkaDataControl was created" );
        this.kafkaTemplate = this.kafkaTemplate();
        this.getNewTopic( "face_events" );
        this.getNewTopic( "car_events" ); }

    private KafkaTemplate< String, String > kafkaTemplate () {
        Map< String, Object > map = new HashMap<>();
        map.put( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.PATH );
        map.put( ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class );
        map.put( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class );
        return new KafkaTemplate<>( new DefaultKafkaProducerFactory<>( map ) ); }

    public CarTotalData writeToKafka ( CarTotalData carTotalData ) {
        if ( Archive.getInstance().getPreferenceItemMapForCar().containsKey( carTotalData.getId() ) ) this.getNewTopic( carTotalData.getId() );
        this.kafkaTemplate.send( carTotalData.getId(), Serdes.getInstance().serialize( carTotalData ) ).addCallback( new ListenableFutureCallback<>() {
            @Override
            public void onFailure( Throwable ex ) { logger.warning("Kafka does not work since: " + LocalDateTime.now() ); }

            @Override
            public void onSuccess( SendResult< String, String > result ) { logger.info("Kafka got: " + carTotalData.getId() + " with offset: " + result.getRecordMetadata().offset() ); }
        } ); return carTotalData; }

    public PsychologyCard writeToKafka ( PsychologyCard psychologyCard ) {
        if ( Archive.getInstance().getPreferenceItemMapForFace().containsKey( psychologyCard.getPinpp() ) ) this.getNewTopic( psychologyCard.getPinpp() );
        this.kafkaTemplate.send( psychologyCard.getModelForPassport().getData().getPerson().getPinpp(), Serdes.getInstance().serialize( psychologyCard ) ).addCallback( new ListenableFutureCallback<>() {
            @Override
            public void onFailure( Throwable ex ) { logger.warning("Kafka does not work since: " + LocalDateTime.now() ); }

            @Override
            public void onSuccess( SendResult< String, String > result ) { logger.info("Kafka got: " + psychologyCard.getModelForPassport().getData().getPerson().getPinpp() + " with offset: " + result.getRecordMetadata().offset() ); }
        } ); return psychologyCard; }

    public void clear () {
        try { Mono.just( this.client.deleteTopics( this.client.listTopics().names().get() ) ).subscribe( System.out::println ); } catch ( InterruptedException | ExecutionException e ) { e.printStackTrace(); }
        finally { this.logger.info( "Kafka was closed" );
            this.kafkaTemplate.destroy();
            this.kafkaTemplate.flush();
            this.properties.clear();
            this.properties = null;
            this.client.close();
            instance = null; } }
}
