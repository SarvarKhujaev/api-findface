package com.ssd.mvd.request;

public final class RequestForModelOfAddress {
    private final String Pcitizen;

    public static RequestForModelOfAddress generate (
            final String pcitizen
    ) {
        return new RequestForModelOfAddress( pcitizen );
    }

    private RequestForModelOfAddress ( final String pcitizen ) {
        this.Pcitizen = pcitizen;
    }
}
