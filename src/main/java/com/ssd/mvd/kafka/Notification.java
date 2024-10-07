package com.ssd.mvd.kafka;

import com.ssd.mvd.interfaces.KafkaCommonMethods;
import com.ssd.mvd.inspectors.TimeInspector;
import com.ssd.mvd.inspectors.Config;

import com.google.gson.annotations.Expose;
import java.util.Date;

public final class Notification extends TimeInspector implements KafkaCommonMethods {
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public Notification setCallingTime( final Date callingTime ) {
        this.callingTime = callingTime;
        return this;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public Notification setPinfl( final String pinfl ) {
        this.pinfl = pinfl;
        return this;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public Notification setReason( final String reason ) {
        this.reason = reason;
        return this;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
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

    public Notification () {}

    @lombok.NonNull
    public String getTopicName() {
        return Config.getERROR_LOGS();
    }


    @lombok.NonNull
    public String getSuccessMessage() {
        return String.join(
                SPACE,
                "Kafka got error: ",
                this.getTopicName(),
                this.toString(),
                " at: ",
                super.newDate().get().toString()
        );
    }


    @lombok.NonNull
    public String getCompletedMessage() {
        return String.join(
                SPACE,
                "Kafka got error: ",
                Config.getERROR_LOGS(),
                this.toString(),
                " at: ",
                super.newDate().get().toString()
        );
    }
}
