package com.ssd.mvd.entity.modelForGai;

import com.ssd.mvd.constants.ErrorResponse;

public final class Insurance {
    private String DateBegin;
    private String DateValid;
    private String TintinType;

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    public void setErrorResponse( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
    }

    private ErrorResponse errorResponse;

    public static Insurance generate (
            final ErrorResponse errorResponse
    ) {
        return new Insurance( errorResponse );
    }

    private Insurance ( final ErrorResponse errorResponse ) {
        this.setErrorResponse( errorResponse );
    }
}
