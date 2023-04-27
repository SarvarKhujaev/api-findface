package com.ssd.mvd.request;

public class RequestForPassport {
    private final String SerialNumber;
    private final String BirthDate;

    public RequestForPassport ( final String serialNumber,
                                final String birthDate ) {
        this.SerialNumber = serialNumber;
        this.BirthDate = birthDate; }
}
