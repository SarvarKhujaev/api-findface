package com.ssd.mvd.request;

public final class RequestForPassport {
    private final String SerialNumber;
    private final String BirthDate;

    public RequestForPassport ( final String value ) {
        this.SerialNumber = value.split( " " )[0];
        this.BirthDate = value.split( " " )[1]; }
}
