package com.ssd.mvd.database;

import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.KafkaStreams;

import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.controller.SerDes;

import java.util.logging.Logger;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

public class KafkaDataControl {
    private Properties properties;
    private final AdminClient client;
    public final String ID = "SSD.FindFace";
    private final KafkaTemplate< String, String > kafkaTemplate;
    private static KafkaDataControl instance = new KafkaDataControl();
    private final Logger logger = Logger.getLogger( KafkaDataControl.class.toString() );
    public final String PATH = "10.254.1.209:9092, 10.254.1.211:9092, 10.254.1.212:9092";

    private KafkaStreams kafkaStreams;
    private final StreamsBuilder builder = new StreamsBuilder();

    private Properties setStreamProperties () {
        this.properties.clear();
        this.properties.put( StreamsConfig.APPLICATION_ID_CONFIG, this.ID );
        this.properties.put( StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, this.PATH );
        this.properties.put( StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, org.apache.kafka.common.serialization.Serdes.String().getClass().getName() );
        this.properties.put( StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, org.apache.kafka.common.serialization.Serdes.String().getClass().getName() );
        return this.properties; }

    private Properties setProperties () {
        this.properties = new Properties();
        this.properties.put( AdminClientConfig.CLIENT_ID_CONFIG, this.ID );
        this.properties.put( AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, this.PATH );
        return properties; }

    private void getNewTopic ( String imei ) {
        this.client.createTopics( Collections.singletonList( TopicBuilder.name( imei ).partitions(5 ).replicas(3 ).build() ) );
        this.logger.info( "Topic: " + imei + " was created" ); }

    public static KafkaDataControl getInstance () { return instance != null ? instance : ( instance = new KafkaDataControl() ); }

    private KafkaDataControl() {
        this.client = KafkaAdminClient.create( this.setProperties() );
        this.logger.info( "KafkaDataControl was created" );
        this.kafkaTemplate = this.kafkaTemplate();
        this.getNewTopic( "api_server_findface_face_events_0.0.1" );
        this.getNewTopic( "api_server_findface_car_events_0.0.1" );
        this.getNewTopic( "test" );
        this.start(); }

    private void start () {
        KStream< String, String > kStream = this.builder.stream( "api_server_findface_car_events_0.0.1", Consumed.with( org.apache.kafka.common.serialization.Serdes.String(), org.apache.kafka.common.serialization.Serdes.String() ) );
        kStream.mapValues( s -> Archive.getInstance().save( SerDes.getSerDes().deserializePreferenceItem( s ) ) ).to( "test", Produced.with( org.apache.kafka.common.serialization.Serdes.String(), org.apache.kafka.common.serialization.Serdes.String() ) );
        this.kafkaStreams = new KafkaStreams( this.builder.build(), this.setStreamProperties() );
        this.kafkaStreams.start(); }

    private KafkaTemplate< String, String > kafkaTemplate () {
        Map< String, Object > map = new HashMap<>();
        map.put( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.PATH );
        map.put( ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class );
        map.put( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class );
        return new KafkaTemplate<>( new DefaultKafkaProducerFactory<>( map ) ); }

    public PsychologyCard writeToKafka ( PsychologyCard psychologyCard ) {
        if ( Archive.getInstance().getPreferenceItemMapForFace().containsKey( psychologyCard.getPinpp() ) ) this.getNewTopic( psychologyCard.getPinpp() );
        this.kafkaTemplate.send( psychologyCard.getModelForPassport().getData().getPerson().getPinpp(), Serdes.getInstance().serialize( psychologyCard ) ).addCallback( new ListenableFutureCallback<>() {
            @Override
            public void onFailure( Throwable ex ) { logger.warning("Kafka does not work since: " + LocalDateTime.now() ); }

            @Override
            public void onSuccess( SendResult< String, String > result ) { logger.info("Kafka got: " + psychologyCard.getModelForPassport().getData().getPerson().getPinpp() + " with offset: " + result.getRecordMetadata().offset() ); }
        } ); return psychologyCard; }

    public void clear () {
        this.logger.info( "Kafka was closed" );
        this.kafkaTemplate.destroy();
        this.kafkaTemplate.flush();
        this.kafkaStreams.close();
        this.properties.clear();
        this.properties = null;
        this.client.close();
        instance = null; }
}
