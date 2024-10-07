package com.ssd.mvd.entity.response;

@lombok.Data
@lombok.Builder
public final class Status {
    private long code;
    private String message;
}
