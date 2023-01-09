package com.ssd.mvd.request;

import lombok.Data;

@Data
public class RequestForPassport {
    private final String SerialNumber;
    private final String BirthDate;

    public RequestForPassport ( String serialNumber, String birthDate ) {
        this.SerialNumber = serialNumber;
        this.BirthDate = birthDate; }
}
