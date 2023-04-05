package com.ssd.mvd.request;

@lombok.Data
public class RequestForModelOfAddress {
    private final String Pcitizen;

    public RequestForModelOfAddress ( String pcitizen ) { this.Pcitizen = pcitizen; }
}
