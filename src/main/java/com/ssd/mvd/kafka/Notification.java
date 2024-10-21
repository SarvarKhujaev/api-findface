package com.ssd.mvd.kafka;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.annotations.AvroMethodAnnotation;
import com.ssd.mvd.annotations.AvroFieldAnnotation;

import com.ssd.mvd.interfaces.KafkaCommonMethods;

import com.ssd.mvd.inspectors.AnnotationInspector;
import com.ssd.mvd.inspectors.TimeInspector;
import com.ssd.mvd.inspectors.Config;

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

    @AvroMethodAnnotation( name = "callingTime" )
    public Date getCallingTime() {
        return this.callingTime;
    }

    @AvroMethodAnnotation( name = "pinfl" )
    public String getPinfl() {
        return this.pinfl;
    }

    @AvroMethodAnnotation( name = "reason" )
    public String getReason() {
        return this.reason;
    }

    @AvroMethodAnnotation( name = "methodName" )
    public String getMethodName() {
        return this.methodName;
    }

    @AvroFieldAnnotation( name = "callingTime", isDate = true )
    private Date callingTime;

    @AvroFieldAnnotation( name = "pinfl" )
    private String pinfl;
    @AvroFieldAnnotation( name = "reason" )
    private String reason;
    @AvroFieldAnnotation( name = "methodName" )
    private String methodName;

    @EntityConstructorAnnotation
    public <T> Notification ( @lombok.NonNull final Class<T> instance ) {
        AnnotationInspector.checkCallerPermission( instance, Notification.class );
        AnnotationInspector.checkAnnotationIsImmutable( Notification.class );
    }

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
