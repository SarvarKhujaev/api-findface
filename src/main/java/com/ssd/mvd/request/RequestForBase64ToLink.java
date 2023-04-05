package com.ssd.mvd.request;

@lombok.Data
public final class RequestForBase64ToLink {
    private final String serviceName;
    private final String photo;

    public RequestForBase64ToLink ( String serviceName, String photo ) {
        this.serviceName = serviceName;
        this.photo = photo; }
}
