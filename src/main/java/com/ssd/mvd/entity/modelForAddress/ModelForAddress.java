package com.ssd.mvd.entity.modelForAddress;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.annotations.WeakReferenceAnnotation;

import com.ssd.mvd.entity.modelForPassport.RequestGuid;
import com.ssd.mvd.entity.PermanentRegistration;

import com.ssd.mvd.inspectors.AnnotationInspector;
import com.ssd.mvd.inspectors.CollectionsInspector;
import com.ssd.mvd.inspectors.CustomSerializer;

import com.ssd.mvd.interfaces.EntityCommonMethods;

import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.extern.jackson.Jacksonized;
import java.util.List;

@Jacksonized
@lombok.Builder
@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public final class ModelForAddress implements EntityCommonMethods< ModelForAddress > {
    public com.ssd.mvd.entity.PermanentRegistration getPermanentRegistration() {
        return this.PermanentRegistration;
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public ModelForAddress setErrorResponse( @lombok.NonNull final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    public List< com.ssd.mvd.entity.modelForAddress.TemproaryRegistration > getTemproaryRegistration() {
        return this.TemproaryRegistration;
    }

    @WeakReferenceAnnotation( name = "RequestGuid", isCollection = false )
    private RequestGuid RequestGuid;
    @WeakReferenceAnnotation( name = "PermanentRegistration", isCollection = false )
    private PermanentRegistration PermanentRegistration;

    @JsonDeserialize
    @WeakReferenceAnnotation( name = "TemproaryRegistration" )
    private List< com.ssd.mvd.entity.modelForAddress.TemproaryRegistration > TemproaryRegistration;

    @WeakReferenceAnnotation( name = "errorResponse", isCollection = false )
    private ErrorResponse errorResponse;

    @EntityConstructorAnnotation
    public <T> ModelForAddress ( @lombok.NonNull final Class<T> instance ) {
        AnnotationInspector.checkCallerPermission( instance, ModelForAddress.class );
        AnnotationInspector.checkAnnotationIsImmutable( ModelForAddress.class );
    }

    private ModelForAddress () {}

    @Override
    @lombok.NonNull
    public Methods getMethodName () {
        return Methods.GET_MODEL_FOR_ADDRESS;
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public ModelForAddress generate (
            @lombok.NonNull final String response
    ) {
        return CustomSerializer.deserialize(
                response.substring( response.indexOf( "Data" ) + 6, response.indexOf( ",\"AnswereId" ) ),
                this.getClass()
        );
    }

    @Override
    @lombok.NonNull
    public ModelForAddress generate () {
        return new ModelForAddress();
    }

    @Override
    public void close() {
        CollectionsInspector.checkAndClear( this.getTemproaryRegistration() );
    }
}