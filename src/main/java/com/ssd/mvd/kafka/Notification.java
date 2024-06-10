package com.ssd.mvd.kafka;

import java.util.Date;

public final class Notification {
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

    private Date callingTime;

    private String pinfl;
    private String reason;
    private String methodName;
}
