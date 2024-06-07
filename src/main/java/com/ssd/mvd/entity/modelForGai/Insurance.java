package com.ssd.mvd.entity.modelForGai;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.constants.ErrorResponse;

public final class Insurance implements EntityCommonMethods< Insurance > {
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

    @Override
    public Insurance generate (
            final ErrorResponse errorResponse
    ) {
        return new Insurance( errorResponse );
    }

    private Insurance ( final ErrorResponse errorResponse ) {
        this.setErrorResponse( errorResponse );
    }

    public Insurance () {}
}
