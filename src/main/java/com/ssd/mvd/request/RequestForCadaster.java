package com.ssd.mvd.request;

import com.ssd.mvd.interfaces.RequestCommonMethods;

public final class RequestForCadaster implements RequestCommonMethods< RequestForCadaster, String > {
    private String Pcadastre;

    @Override
    public RequestForCadaster generate (
            final String pcadastre
    ) {
        this.Pcadastre = pcadastre;
        return this;
    }

    public RequestForCadaster () {}
}
