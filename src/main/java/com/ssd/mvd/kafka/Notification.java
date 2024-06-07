package com.ssd.mvd.kafka;

import java.util.Date;

public final class Notification {
    public Date getCallingTime() {
        return this.callingTime;
    }

    public Notification setCallingTime( final Date callingTime ) {
        this.callingTime = callingTime;
        return this;
    }

    public String getPinfl() {
        return this.pinfl;
    }

    public Notification setPinfl( final String pinfl ) {
        this.pinfl = pinfl;
        return this;
    }

    public String getReason() {
        return this.reason;
    }

    public Notification setReason( final String reason ) {
        this.reason = reason;
        return this;
    }

    public String getMethodName() {
        return this.methodName;
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
