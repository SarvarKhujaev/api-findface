package com.ssd.mvd.database;

import com.google.gson.Gson;
import com.ssd.mvd.entity.modelForFindFace.PreferenceItem;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class KafkaConsumer implements Runnable {
    private Boolean flag = true;
    private final Gson gson = new Gson();
    private ConsumerRecords< String, String > records;
    private final Logger logger = Logger.getLogger( KafkaConsumer.class.toString() );
    private final org.apache.kafka.clients.consumer.KafkaConsumer< String, String > kafkaConsumer;

    private Map< String, Object > consumer () {
        Map< String, Object > props = new HashMap<>();
        props.put( ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false ); // this parameter is response for auto commit all offsets
        props.put( ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest" ); // consumer will start to read only fresh data from kafka
        props.put( ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, false ); // disable to create new topic for consumer
        props.put( ConsumerConfig.GROUP_ID_CONFIG, KafkaDataControl.getInstance().ID ); // common ID for all consumers
        props.put( ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaDataControl.getInstance().PATH ); // ip and port
        props.put( ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class );
        props.put( ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class );
        return props; }

    public KafkaConsumer( String imei ) {
        // create new Consumer for Tracker and subscribe it to its current Topic
        ( this.kafkaConsumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>( this.consumer() ) ).subscribe( Collections.singletonList( String.valueOf( imei ) ) ); // create new consumer and subscribe for each tracker
        this.logger.info( "Consumer: " + imei + " was created" ); }

    private void readFromKafka () {
        if ( !( this.records = this.kafkaConsumer.poll( Duration.ofSeconds( 2 ) ) ).isEmpty() ) {
            records.forEach( record -> Archive.getInstance().save( this.gson.fromJson( record.value(), PreferenceItem.class ) ) ); // saving all data toDataSave class
            this.kafkaConsumer.commitAsync( ( map, e ) -> { if ( e != null ) this.logger.info( "Smth wrong during reading of offset: " + e.getCause() ); } ); } } // committing offset asynchronously

    public void clear () {
        this.flag = false;
        this.records = null;
        this.kafkaConsumer.close();
        this.logger.info( "The consumer is closed" ); }

    @Override
    public void run () {
        this.kafkaConsumer.seekToBeginning( this.kafkaConsumer.assignment() );
        while ( this.flag ) {
            this.readFromKafka();
            try { Thread.sleep( 10 * 1000 ); } catch ( InterruptedException e ) { throw new RuntimeException(e); } }
        this.kafkaConsumer.commitAsync(); // commits all offsets before cleaning
        this.clear(); } // cleaning the consumer memory
}
