package com.ssd.mvd;

import com.ssd.mvd.entityForLogging.UserRequest;
import com.ssd.mvd.inspectors.EntitiesInstances;
import com.ssd.mvd.inspectors.StringOperations;
import com.ssd.mvd.entityForLogging.ErrorLog;
import com.ssd.mvd.entity.ApiResponseModel;
import com.ssd.mvd.kafka.KafkaDataControl;
import com.ssd.mvd.entity.Status;

import junit.framework.TestCase;

import java.util.Date;
import java.util.UUID;

public final class KafkaConnectionTest extends TestCase {
    private final UUID uuid = UUID.randomUUID();

    @Override
    public void setUp () {
        super.setName( KafkaDataControl.getKafkaDataControl().getClass().getName() );
    }

    @Override
    public void tearDown () {
        /*
        closing connection to Kafka
        */
        KafkaDataControl.getKafkaDataControl().close();
    }

    public void testKafkaConnection () {
        assertNotNull( KafkaDataControl.getKafkaDataControl() );
    }

    public void testSendMessagesToKafka () {
        KafkaDataControl
                .getKafkaDataControl()
                .sendMessage(
                        EntitiesInstances.NOTIFICATION
                                .setPinfl( this.uuid.toString() )
                                .setReason( this.uuid.toString() )
                                .setMethodName( this.uuid.toString() )
                                .setCallingTime( new Date() )
                );

        KafkaDataControl
                .getKafkaDataControl()
                .sendMessage( new ErrorLog( StringOperations.EMPTY ) );

        KafkaDataControl
                .getKafkaDataControl()
                .sendMessage(
                        new UserRequest(
                                EntitiesInstances.PSYCHOLOGY_CARD,
                                ApiResponseModel
                                        .builder()
                                        .status(
                                                Status
                                                        .builder()
                                                        .code( 200L )
                                                        .message( "30096545789812" )
                                                        .build()
                                        ).success( true )
                                        .build()
                        )
                );
    }
}
