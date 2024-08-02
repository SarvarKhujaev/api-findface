package com.ssd.mvd.entityForLogging;

import com.google.gson.annotations.Expose;

import com.ssd.mvd.inspectors.Config;
import com.ssd.mvd.interfaces.KafkaCommonMethods;

public class ErrorLog extends Config implements KafkaCommonMethods {
    @Expose
    private String errorMessage;

    @Expose
    private final static String integratedService = IntegratedServiceApis.OVIR.getName();

    @Expose
    private final static String integratedServiceApiDescription = IntegratedServiceApis.OVIR.getDescription();

    public ErrorLog ( final String errorMessage ) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getTopicName() {
        return super.getADMIN_PANEL_ERROR_LOG();
    }

    @Override
    public String getSuccessMessage() {
        return String.join(
                " ",
                "Kafka got error for : ",
                super.getADMIN_PANEL_ERROR_LOG(),
                this.errorMessage,
                " at: ",
                super.newDate().toString(),
                integratedService,
                integratedServiceApiDescription
        );
    }

    @Override
    public String getCompletedMessage() {
        return String.join(
                " ",
                "Kafka got error for : ",
                super.getADMIN_PANEL_ERROR_LOG(),
                this.errorMessage,
                " at: ",
                super.newDate().toString(),
                integratedService,
                integratedServiceApiDescription
        );
    }
}