package com.ssd.mvd.request;

import com.ssd.mvd.interfaces.RequestCommonMethods;

public final class RequestForModelOfAddress implements RequestCommonMethods< RequestForModelOfAddress, String > {
    private String Pcitizen;

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public RequestForModelOfAddress generate (
            @lombok.NonNull final String pcitizen
    ) {
        return new RequestForModelOfAddress( pcitizen );
    }

    private RequestForModelOfAddress ( final String pcitizen ) {
        this.Pcitizen = pcitizen;
    }

    public RequestForModelOfAddress () {}
}
