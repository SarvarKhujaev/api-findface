package com.ssd.mvd.entity.modelForGai;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.inspectors.Config;
import com.ssd.mvd.constants.Errors;

public final class Insurance
        extends Config
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

    public Insurance setErrorResponse( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    private ErrorResponse errorResponse;

    public Insurance () {}

    @Override
    public Insurance generate(
            final String message,
            final Errors errors
    ) {
        return this.generate().setErrorResponse(
                super.error.apply(
                        message,
                        errors
                )
        );
    }

    @Override
    public Insurance generate() {
        return new Insurance();
    }

    @Override
    public Methods getMethodName() {
        return Methods.GET_INSURANCE;
    }

    @Override
    public String getMethodApi() {
        return super.getAPI_FOR_FOR_INSURANCE();
    }

    @Override
    public Insurance generate(
            final String response
    ) {
        return !response.contains( "топилмади" )
                ? super.deserialize( response, this.getClass() )
                : this.generate().generate( response, Errors.DATA_NOT_FOUND );
    }

    @Override
    public Insurance generate (
            final ErrorResponse errorResponse
    ) {
        return this.generate().setErrorResponse( errorResponse );
    }
}
