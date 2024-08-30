package com.ssd.mvd.entity.modelForCadastr;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.inspectors.Config;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;

import java.util.List;

public final class Data
        extends Config
        implements EntityCommonMethods< Data >, ServiceCommonMethods {
    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public Data setErrorResponse ( @lombok.NonNull final ErrorResponse errorResponse ) {
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
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _ -> !null" )
    public Data generate(
            @lombok.NonNull final String message,
            @lombok.NonNull final Errors errors
    ) {
        return this.generate().setErrorResponse(
                super.error.apply(
                        message,
                        errors
                )
        );
    }

    @Override
    @lombok.NonNull
    public Data generate() {
        return new Data();
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    public Data generate (
            @lombok.NonNull final ErrorResponse errorResponse
    ) {
        return this.setErrorResponse( errorResponse );
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    public Data generate (
            @lombok.NonNull final String response
    ) {
        return super.deserialize(
                response.substring( response.indexOf( "Data" ) + 6, response.indexOf( ",\"AnswereId" ) ),
                this.getClass()
        );
    }

    @Override
    @lombok.NonNull
    public Methods getMethodName() {
        return Methods.CADASTER;
    }

    @Override
    @lombok.NonNull
    public String getMethodApi() {
        return super.getAPI_FOR_CADASTR();
    }

    @Override
    public void close() {
        this.getTemproaryRegistration().clear();
        this.getPermanentRegistration().clear();
    }
}
