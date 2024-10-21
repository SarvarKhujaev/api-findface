package com.ssd.mvd.entity.modelForPassport;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.annotations.WeakReferenceAnnotation;

import com.ssd.mvd.inspectors.AnnotationInspector;
import com.ssd.mvd.inspectors.CustomSerializer;

import com.ssd.mvd.interfaces.EntityCommonMethods;

import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;

public final class ModelForPassport implements EntityCommonMethods< ModelForPassport > {
    public com.ssd.mvd.entity.modelForPassport.Data getData () {
        return this.Data;
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public ModelForPassport setErrorResponse( @lombok.NonNull final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    public int getAnswereId() {
        return this.AnswereId;
    }

    public String getAnswereMessage() {
        return this.AnswereMessage;
    }

    public String getAnswereComment() {
        return this.AnswereComment;
    }

    private int AnswereId;
    private String AnswereMessage;
    private String AnswereComment;

    @JsonDeserialize
    @WeakReferenceAnnotation( name = "Data", isCollection = false )
    private com.ssd.mvd.entity.modelForPassport.Data Data;

    @WeakReferenceAnnotation( name = "errorResponse", isCollection = false )
    private ErrorResponse errorResponse;

    @EntityConstructorAnnotation
    public <T> ModelForPassport ( @lombok.NonNull final Class<T> instance ) {
        AnnotationInspector.checkCallerPermission( instance, ModelForPassport.class );
        AnnotationInspector.checkAnnotationIsImmutable( ModelForPassport.class );
    }

    private ModelForPassport () {}

    @Override
    @lombok.NonNull
    public ModelForPassport generate() {
        return new ModelForPassport();
    }

    @Override
    @lombok.NonNull
    public Methods getMethodName() {
        return Methods.GET_MODEL_FOR_PASSPORT;
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public ModelForPassport generate(
            @lombok.NonNull final String response
    ) {
        return CustomSerializer.deserialize( response, this.getClass() );
    }
}
