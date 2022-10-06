package com.ssd.mvd.kafka;

import lombok.Data;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.Collections;
import java.time.LocalDateTime;
import java.util.logging.Logger;
import com.ssd.mvd.FindFaceServiceApplication;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Data
public class KafkaDataControl {
    private final AdminClient client;
    private final KafkaTemplate< String, String > kafkaTemplate;
    private static KafkaDataControl instance = new KafkaDataControl();
    private final Logger logger = Logger.getLogger( KafkaDataControl.class.toString() );

    public static KafkaDataControl getInstance () { return instance != null ? instance : ( instance = new KafkaDataControl() ); }

    private final String PATH = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.KAFKA_BROKER" );

    private final String ID = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.GROUP_ID_FOR_KAFKA" );

    private final String activeTask = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.ERROR_LOGS" );

    private final String serviceStorageTopic = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.ADMIN_PANEL" );

    private Properties setProperties () {
        Properties properties = new Properties();
        properties.put( AdminClientConfig.CLIENT_ID_CONFIG, this.ID );
        properties.put( AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, this.PATH );
        return properties; }

    public void getNewTopic ( String imei ) {
        this.getClient().createTopics( Collections.singletonList( TopicBuilder
                .name( imei )
                .partitions(5 )
                .replicas(3 )
                .build() ) );
        this.logger.info( "Topic: " + imei + " was created" ); }

    private KafkaTemplate< String, String > kafkaTemplate () {
        Map< String, Object > map = new HashMap<>();
        map.put( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.PATH );
        map.put( ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class );
        map.put( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class );
        return new KafkaTemplate<>( new DefaultKafkaProducerFactory<>( map ) ); }

    private KafkaDataControl() {
        this.kafkaTemplate = this.kafkaTemplate();
        this.logger.info( "KafkaDataControl was created" );
        this.client = KafkaAdminClient.create( this.setProperties() );
        this.getNewTopic( this.getServiceStorageTopic() );
        this.getNewTopic( this.getActiveTask() ); }

    public void writeToKafka ( String card ) {
        this.kafkaTemplate.send( this.getActiveTask(), card ).addCallback( new ListenableFutureCallback<>() {
            @Override
            public void onSuccess( org.springframework.kafka.support.SendResult< String, String > result ) {
                logger.info( "Kafka got notification: " + card ); }

            @Override
            public void onFailure( Throwable ex ) { logger.warning("Kafka does not work since: " + LocalDateTime.now() ); } } ); }

    public void writeToKafkaServiceUsage ( String serviceUsage ) {
        this.kafkaTemplate.send( this.getServiceStorageTopic(), serviceUsage )
                .addCallback( new ListenableFutureCallback<>() {
            @Override
            public void onSuccess( org.springframework.kafka.support.SendResult< String, String > result ) {
                logger.info( "New user exposed your service: " + serviceUsage ); }

            @Override
            public void onFailure( Throwable ex ) { logger.warning("Kafka does not work since: " + LocalDateTime.now() ); } } ); }
}
