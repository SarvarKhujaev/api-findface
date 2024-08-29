package com.ssd.mvd.request;

import com.ssd.mvd.interfaces.RequestCommonMethods;

public final class RequestForCadaster implements RequestCommonMethods< RequestForCadaster, String > {
    private String Pcadastre;

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public RequestForCadaster generate (
            @lombok.NonNull final String pcadastre
    ) {
        this.Pcadastre = pcadastre;
        return this;
    }

    public RequestForCadaster () {}
}
