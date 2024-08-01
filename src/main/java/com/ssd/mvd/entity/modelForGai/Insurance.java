package com.ssd.mvd.entity.modelForGai;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.inspectors.ErrorController;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Errors;

public final class Insurance
        extends ErrorController
        implements EntityCommonMethods< Insurance > {
    public String getDateBegin() {
        return this.DateBegin;
    }

    public String getDateValid() {
        return this.DateValid;
    }

    public String getTintinType() {
        return this.TintinType;
    }

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
    public Insurance generate(
            final String message,
            final Errors errors
    ) {
        return new Insurance().generate(
                super.error.apply(
                        message,
                        errors
                )
        );
    }

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
