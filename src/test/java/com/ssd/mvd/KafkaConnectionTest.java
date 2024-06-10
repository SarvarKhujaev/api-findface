package com.ssd.mvd;

import com.ssd.mvd.entityForLogging.IntegratedServiceApis;
import com.ssd.mvd.entityForLogging.UserRequest;
import com.ssd.mvd.entityForLogging.ErrorLog;
import com.ssd.mvd.entity.ApiResponseModel;
import com.ssd.mvd.kafka.KafkaDataControl;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.kafka.Notification;
import com.ssd.mvd.controller.SerDes;
import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.entity.Status;
import junit.framework.TestCase;

import java.util.Date;
import java.util.UUID;

public final class KafkaConnectionTest extends TestCase {
    private final UUID uuid = UUID.randomUUID();

    @Override
    public void setUp () {
        super.setName( KafkaDataControl.getInstance().getClass().getName() );
    }

    @Override
    public void tearDown () {
        /*
        closing connection to Kafka
        */
        KafkaDataControl.getInstance().close();
    }

    public void testKafkaConnection () {
        assertNotNull( KafkaDataControl.getInstance() );
    }

    public void testSendMessagesToKafka () {
        KafkaDataControl
                .getInstance()
                .writeErrorLog
                .accept(
                        SerDes
                                .getSerDes()
                                .getGson()
                                .toJson(
                                        new Notification()
                                                .setPinfl( this.uuid.toString() )
                                                .setReason( this.uuid.toString() )
                                                .setMethodName( this.uuid.toString() )
                                                .setCallingTime( new Date() )
                                )
                );

        KafkaDataControl
                .getInstance()
                .writeToKafkaErrorLog
                .accept(
                        SerDes
                                .getSerDes()
                                .getGson()
                                .toJson(
                                        ErrorLog
                                                .builder()
                                                .createdAt( new Date().getTime() )
                                                .errorMessage( Errors.SERVICE_WORK_ERROR.getErrorMEssage( "" ) )
                                                .integratedService( IntegratedServiceApis.OVIR.getName() )
                                                .integratedServiceApiDescription( IntegratedServiceApis.OVIR.getDescription() )
                                                .build()
                                )
                );

        KafkaDataControl
                .getInstance()
                .writeToKafkaServiceUsage
                .accept(
                        SerDes
                                .getSerDes()
                                .getGson()
                                .toJson(
                                        new UserRequest(
                                                new PsychologyCard(),
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
                                )
                );
    }
}
