package com.ssd.mvd.entity.modelForFioOfPerson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.annotations.WeakReferenceAnnotation;

import com.ssd.mvd.inspectors.AnnotationInspector;
import com.ssd.mvd.interfaces.EntityCommonMethods;

import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;

import java.util.List;

@JsonIgnoreProperties( ignoreUnknown = true )
public final class PersonTotalDataByFIO implements EntityCommonMethods< PersonTotalDataByFIO > {
    public List< Person > getData() {
        return this.Data;
    }

    public void setData( final List< Person > data ) {
        this.Data = data;
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public PersonTotalDataByFIO setErrorResponse( @lombok.NonNull final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    public int getAnswereId() {
        return AnswereId;
    }

    public String getAnswereMessage() {
        return AnswereMessage;
    }

    public String getAnswereComment() {
        return AnswereComment;
    }

    private int AnswereId;
    private String AnswereMessage;
    private String AnswereComment;

    @JsonDeserialize
    @WeakReferenceAnnotation( name = "Data" )
    private List< Person > Data;

    @WeakReferenceAnnotation( name = "errorResponse", isCollection = false )
    private ErrorResponse errorResponse;

    private PersonTotalDataByFIO () {}

    @EntityConstructorAnnotation
    public <T> PersonTotalDataByFIO ( @lombok.NonNull final Class<T> instance ) {
        AnnotationInspector.checkCallerPermission( instance, PersonTotalDataByFIO.class );
        AnnotationInspector.checkAnnotationIsImmutable( PersonTotalDataByFIO.class );
    }

    private PersonTotalDataByFIO ( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
    }

    @Override
    @lombok.NonNull
    public PersonTotalDataByFIO generate() {
        return new PersonTotalDataByFIO();
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public PersonTotalDataByFIO generate (
            @lombok.NonNull final ErrorResponse errorResponse
    ) {
        return new PersonTotalDataByFIO( errorResponse );
    }

    @Override
    @lombok.NonNull
    public Methods getMethodName() {
        return Methods.GET_PERSON_TOTAL_DATA_BY_FIO;
    }
}
