package com.ssd.mvd.request;

@lombok.Data
public class RequestForCadaster {
    private final String Pcadastre;

    public RequestForCadaster ( String pcadastre ) { this.Pcadastre = pcadastre; }
}
