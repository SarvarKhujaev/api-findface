package com.ssd.mvd.entity.modelForCadastr;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.interfaces.EntityCommonMethods;

import com.ssd.mvd.inspectors.CustomSerializer;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;

public final class Data implements EntityCommonMethods< Data >, ServiceCommonMethods {
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

    public CopyOnWriteArrayList< Person > getPermanentRegistration() {
        return this.PermanentRegistration;
    }

    public CopyOnWriteArrayList< com.ssd.mvd.entity.modelForCadastr.TemproaryRegistration > getTemproaryRegistration() {
        return this.TemproaryRegistration;
    }

    private ErrorResponse errorResponse;

    @JsonDeserialize
    private CopyOnWriteArrayList< Person > PermanentRegistration;

    @JsonDeserialize
    private CopyOnWriteArrayList< TemproaryRegistration > TemproaryRegistration;

    public Data () {}

    @Override
    @lombok.NonNull
    public Data generate() {
        return new Data();
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    public Data generate (
            @lombok.NonNull final String response
    ) {
        return CustomSerializer.deserialize(
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
    public void close() {
        this.getTemproaryRegistration().clear();
        this.getPermanentRegistration().clear();
    }
}
