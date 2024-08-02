package com.ssd.mvd.entity.modelForCadastr;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.inspectors.ErrorController;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;

import java.util.List;

public final class Data
        extends ErrorController
        implements EntityCommonMethods< Data >, ServiceCommonMethods {
    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    public Data setErrorResponse ( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    public List< Person > getPermanentRegistration() {
        return this.PermanentRegistration;
    }

    public List< com.ssd.mvd.entity.modelForCadastr.TemproaryRegistration > getTemproaryRegistration() {
        return this.TemproaryRegistration;
    }

    private ErrorResponse errorResponse;

    @JsonDeserialize
    private List< Person > PermanentRegistration;

    @JsonDeserialize
    private List< TemproaryRegistration > TemproaryRegistration;

    public Data () {}

    @Override
    public Data generate(
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
    public Data generate() {
        return new Data();
    }

    @Override
    public Data generate (
            final ErrorResponse errorResponse
    ) {
        return this.setErrorResponse( errorResponse );
    }

    @Override
    public Data generate (
            final String response
    ) {
        return super.deserialize(
                response.substring( response.indexOf( "Data" ) + 6, response.indexOf( ",\"AnswereId" ) ),
                this.getClass()
        );
    }

    @Override
    public Methods getMethodName() {
        return Methods.CADASTER;
    }

    @Override
    public void close() {
        this.getTemproaryRegistration().clear();
        this.getPermanentRegistration().clear();
    }
}
