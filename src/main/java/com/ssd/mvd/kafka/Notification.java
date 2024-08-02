package com.ssd.mvd.kafka;

import com.ssd.mvd.interfaces.KafkaCommonMethods;
import com.ssd.mvd.inspectors.Config;

import com.google.gson.annotations.Expose;
import java.util.Date;

public final class Notification extends Config implements KafkaCommonMethods {
    public Notification setCallingTime( final Date callingTime ) {
        this.callingTime = callingTime;
        return this;
    }

    public Notification setPinfl( final String pinfl ) {
        this.pinfl = pinfl;
        return this;
    }

    public Notification setReason( final String reason ) {
        this.reason = reason;
        return this;
    }

    public Notification setMethodName( final String methodName ) {
        this.methodName = methodName;
        return this;
    }

    @Expose
    private Date callingTime;

    @Expose
    private String pinfl;
    @Expose
    private String reason;
    @Expose
    private String methodName;

    @Override
    public String getTopicName() {
        return super.getERROR_LOGS();
    }

    @Override
    public String getSuccessMessage() {
        return String.join(
                " ",
                "Kafka got error: ",
                this.getTopicName(),
                this.toString(),
                " at: ",
                super.newDate().toString()
        );
    }

    @Override
    public String getCompletedMessage() {
        return String.join(
                " ",
                "Kafka got error: ",
                super.getERROR_LOGS(),
                this.toString(),
                " at: ",
                super.newDate().toString()
        );
    }
}
