package com.ssd.mvd.request;

import lombok.Data;

@Data
public class RequestForModelOfAddress {
    private final String Pcitizen;

    public RequestForModelOfAddress ( String pcitizen ) { this.Pcitizen = pcitizen; }
}
