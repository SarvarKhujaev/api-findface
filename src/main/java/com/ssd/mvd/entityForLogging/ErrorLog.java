package com.ssd.mvd.entityForLogging;

import com.ssd.mvd.annotations.AvroFieldAnnotation;
import com.ssd.mvd.annotations.AvroMethodAnnotation;

import com.ssd.mvd.inspectors.Config;
import com.ssd.mvd.inspectors.TimeInspector;
import com.ssd.mvd.interfaces.KafkaCommonMethods;

public class ErrorLog extends TimeInspector implements KafkaCommonMethods {
    @AvroMethodAnnotation( name = "errorMessage" )
    public String getErrorMessage() {
        return errorMessage;
    }

    @AvroMethodAnnotation( name = "INTEGRATED_SERVICE" )
    public String getINTEGRATED_SERVICE() {
        return INTEGRATED_SERVICE;
    }

    @AvroMethodAnnotation( name = "INTEGRATED_SERVICE_API_DESCRIPTION" )
    public String getINTEGRATED_SERVICE_API_DESCRIPTION() {
        return INTEGRATED_SERVICE_API_DESCRIPTION;
    }

    @AvroFieldAnnotation( name = "errorMessage" )
    private String errorMessage;

    @AvroFieldAnnotation( name = "INTEGRATED_SERVICE" )
    private static final String INTEGRATED_SERVICE = IntegratedServiceApis.OVIR.getName();

    @AvroFieldAnnotation( name = "INTEGRATED_SERVICE_API_DESCRIPTION" )
    private static final String INTEGRATED_SERVICE_API_DESCRIPTION = IntegratedServiceApis.OVIR.getDescription();

    public ErrorLog ( @lombok.NonNull final String errorMessage ) {
        super( ErrorLog.class );
        this.errorMessage = errorMessage;
    }

    @Override
    @lombok.NonNull
    public String getTopicName() {
        return Config.getADMIN_PANEL_ERROR_LOG();
    }

    @Override
    @lombok.NonNull
    public String getSuccessMessage() {
        return String.join(
                SPACE,
                "Kafka got error for : ",
                Config.getADMIN_PANEL_ERROR_LOG(),
                this.errorMessage,
                " at: ",
                super.newDate().get().toString(),
                INTEGRATED_SERVICE,
                INTEGRATED_SERVICE_API_DESCRIPTION
        );
    }

    @Override
    @lombok.NonNull
    public String getCompletedMessage() {
        return String.join(
                SPACE,
                "Kafka got error for : ",
                Config.getADMIN_PANEL_ERROR_LOG(),
                this.errorMessage,
                " at: ",
                super.newDate().toString(),
                INTEGRATED_SERVICE,
                INTEGRATED_SERVICE_API_DESCRIPTION
        );
    }
}