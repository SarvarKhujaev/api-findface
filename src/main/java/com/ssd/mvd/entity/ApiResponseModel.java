package com.ssd.mvd.entity;

@lombok.Builder
@lombok.Data
public class ApiResponseModel {
    private Boolean success;
    private Status status;
    private User user;

    public ApiResponseModel changeMessage( final String message ) {
        this.getStatus().setMessage( message );
        return this; }
}
