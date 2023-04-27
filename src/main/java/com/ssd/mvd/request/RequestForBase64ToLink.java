package com.ssd.mvd.request;

public final class RequestForBase64ToLink {
    private final String serviceName;
    private final String photo;

    public RequestForBase64ToLink ( final String serviceName, final String photo ) {
        this.serviceName = serviceName;
        this.photo = photo; }
}
