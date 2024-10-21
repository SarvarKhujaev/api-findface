package com.ssd.mvd.entity.modelForCadastr;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.annotations.WeakReferenceAnnotation;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.inspectors.*;

import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;

public final class Data implements EntityCommonMethods< Data > {
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

    @WeakReferenceAnnotation( name = "errorResponse", isCollection = false )
    private ErrorResponse errorResponse;

    @JsonDeserialize
    @WeakReferenceAnnotation( name = "PermanentRegistration" )
    private CopyOnWriteArrayList< Person > PermanentRegistration;

    @JsonDeserialize
    @WeakReferenceAnnotation( name = "TemproaryRegistration" )
    private CopyOnWriteArrayList< TemproaryRegistration > TemproaryRegistration;

    private Data () {}

    @EntityConstructorAnnotation
    public <T> Data ( @lombok.NonNull final Class<T> instance ) {
        AnnotationInspector.checkCallerPermission( instance, Data.class );
        AnnotationInspector.checkAnnotationIsImmutable( Data.class );
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
        CollectionsInspector.checkAndClear( this.getTemproaryRegistration() );
        CollectionsInspector.checkAndClear( this.getPermanentRegistration() );
    }
}
