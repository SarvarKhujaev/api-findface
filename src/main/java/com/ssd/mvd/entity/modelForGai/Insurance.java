package com.ssd.mvd.entity.modelForGai;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.annotations.WeakReferenceAnnotation;
import com.ssd.mvd.interfaces.EntityCommonMethods;

import com.ssd.mvd.inspectors.AnnotationInspector;
import com.ssd.mvd.inspectors.CustomSerializer;

import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;

public final class Insurance implements EntityCommonMethods< Insurance > {
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

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public Insurance setErrorResponse( @lombok.NonNull final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    @WeakReferenceAnnotation( name = "errorResponse", isCollection = false )
    private ErrorResponse errorResponse;

    @EntityConstructorAnnotation
    public <T> Insurance ( @lombok.NonNull final Class<T> instance ) {
        AnnotationInspector.checkCallerPermission( instance, Insurance.class );
        AnnotationInspector.checkAnnotationIsImmutable( Insurance.class );
    }

    private Insurance () {}

    @Override
    @lombok.NonNull
    public Insurance generate() {
        return new Insurance();
    }

    @Override
    @lombok.NonNull
    public Methods getMethodName() {
        return Methods.GET_INSURANCE;
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public Insurance generate(
            @lombok.NonNull final String response
    ) {
        return !response.contains( "топилмади" )
                ? CustomSerializer.deserialize( response, this.getClass() )
                : this.generate().generate( response, Errors.DATA_NOT_FOUND );
    }
}
