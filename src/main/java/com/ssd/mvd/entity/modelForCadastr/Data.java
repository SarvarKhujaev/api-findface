package com.ssd.mvd.entity.modelForCadastr;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.controller.ErrorController;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Errors;

import java.util.List;

public final class Data
        extends ErrorController
        implements EntityCommonMethods< Data >, ServiceCommonMethods {
    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    public void setErrorResponse ( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
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

    @Override
    public Data generate(
            final String message,
            final Errors errors
    ) {
        return new Data().generate(
                super.error.apply(
                        message,
                        errors
                )
        );
    }

    @Override
    public Data generate (
            final ErrorResponse errorResponse
    ) {
        return new Data( errorResponse );
    }

    private Data ( final ErrorResponse errorResponse ) {
        this.setErrorResponse( errorResponse );
    }

    public Data () {}

    @Override
    public void close() {
        this.getTemproaryRegistration().clear();
        this.getPermanentRegistration().clear();
    }
}
