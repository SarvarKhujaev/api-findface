package com.ssd.mvd.entity.modelForGai;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.interfaces.EntityCommonMethods;

import com.ssd.mvd.inspectors.CollectionsInspector;
import com.ssd.mvd.inspectors.AnnotationInspector;
import com.ssd.mvd.inspectors.CustomSerializer;


import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;

import java.util.List;

public final class ViolationsList implements EntityCommonMethods< ViolationsList > {
    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public ViolationsList setErrorResponse( @lombok.NonNull final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    private ViolationsList setViolationsInformationsList(
            @lombok.NonNull final List< ViolationsInformation > violationsInformationsList
    ) {
        this.violationsInformationsList = violationsInformationsList;
        return this;
    }

    public List< ViolationsInformation > getViolationsInformationsList() {
        return this.violationsInformationsList;
    }

    private ErrorResponse errorResponse;

    @JsonDeserialize
    private List< ViolationsInformation > violationsInformationsList;

    @EntityConstructorAnnotation
    public <T> ViolationsList ( @lombok.NonNull final Class<T> instance ) {
        AnnotationInspector.checkCallerPermission( instance, ViolationsList.class );
        AnnotationInspector.checkAnnotationIsImmutable( ViolationsList.class );
    }

    private ViolationsList () {}

    @Override
    @lombok.NonNull
    public ViolationsList generate() {
        return new ViolationsList();
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public ViolationsList generate(
            @lombok.NonNull final String response
    ) {
        return this.generate().setViolationsInformationsList( CustomSerializer.stringToArrayList( response, ViolationsInformation[].class ) );
    }

    @Override
    @lombok.NonNull
    public Methods getMethodName() {
        return Methods.GET_VIOLATION_LIST;
    }

    @Override
    public void close() {
        CollectionsInspector.checkAndClear( this.getViolationsInformationsList() );
    }
}
