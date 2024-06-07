package com.ssd.mvd.request;

import com.ssd.mvd.interfaces.RequestCommonMethods;

public final class RequestForModelOfAddress implements RequestCommonMethods< RequestForModelOfAddress, String > {
    private String Pcitizen;

    @Override
    public RequestForModelOfAddress generate (
            final String pcitizen
    ) {
        return new RequestForModelOfAddress( pcitizen );
    }

    private RequestForModelOfAddress ( final String pcitizen ) {
        this.Pcitizen = pcitizen;
    }

    public RequestForModelOfAddress () {}
}
