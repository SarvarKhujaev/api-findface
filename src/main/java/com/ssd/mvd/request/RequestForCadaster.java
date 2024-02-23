package com.ssd.mvd.request;

public final class RequestForCadaster {
    private final String Pcadastre;

    public static RequestForCadaster generate (
            final String pcadastre
    ) {
        return new RequestForCadaster( pcadastre );
    }

    private RequestForCadaster ( final String pcadastre ) {
        this.Pcadastre = pcadastre;
    }
}
