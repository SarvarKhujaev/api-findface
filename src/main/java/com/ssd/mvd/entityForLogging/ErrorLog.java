package com.ssd.mvd.entityForLogging;

import com.google.gson.annotations.Expose;

import com.ssd.mvd.inspectors.Config;
import com.ssd.mvd.interfaces.KafkaCommonMethods;

public class ErrorLog extends Config implements KafkaCommonMethods {
    @Expose
    private String errorMessage;

    @Expose
    private final static String INTEGRATED_SERVICE = IntegratedServiceApis.OVIR.getName();

    @Expose
    private final static String INTEGRATED_SERVICE_API_DESCRIPTION = IntegratedServiceApis.OVIR.getDescription();

    public ErrorLog ( @lombok.NonNull final String errorMessage ) {
        this.errorMessage = errorMessage;
    }

    @Override
    @lombok.NonNull
    public String getTopicName() {
        return super.getADMIN_PANEL_ERROR_LOG();
    }

    @Override
    @lombok.NonNull
    public String getSuccessMessage() {
        return String.join(
                " ",
                "Kafka got error for : ",
                super.getADMIN_PANEL_ERROR_LOG(),
                this.errorMessage,
                " at: ",
                super.newDate().toString(),
                INTEGRATED_SERVICE,
                INTEGRATED_SERVICE_API_DESCRIPTION
        );
    }

    @Override
    @lombok.NonNull
    public String getCompletedMessage() {
        return String.join(
                " ",
                "Kafka got error for : ",
                super.getADMIN_PANEL_ERROR_LOG(),
                this.errorMessage,
                " at: ",
                super.newDate().toString(),
                INTEGRATED_SERVICE,
                INTEGRATED_SERVICE_API_DESCRIPTION
        );
    }
}