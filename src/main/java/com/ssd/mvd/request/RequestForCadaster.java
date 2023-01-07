package com.ssd.mvd.request;

import lombok.Data;

@Data
public class RequestForCadaster {
    private final String Pcadastre;

    public RequestForCadaster ( String pcadastre ) { this.Pcadastre = pcadastre; }
}
