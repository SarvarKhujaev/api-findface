package com.ssd.mvd.entity.response;

@lombok.Data
@lombok.Builder
public final class ApiResponseModel {
    private Status status;
    private User user;

    public ApiResponseModel changeMessage ( @lombok.NonNull final String message ) {
        this.getStatus().setMessage( message );
        return this;
    }
}
