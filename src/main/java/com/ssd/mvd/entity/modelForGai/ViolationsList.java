package com.ssd.mvd.entity.modelForGai;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.inspectors.Config;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;

import java.util.List;

public final class ViolationsList
        extends Config
        implements EntityCommonMethods< ViolationsList >, ServiceCommonMethods {
    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    private ViolationsList setErrorResponse( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    private ViolationsList setViolationsInformationsList(
            final List< ViolationsInformation > violationsInformationsList
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

    public ViolationsList () {}

    @Override
    @lombok.NonNull
    public ViolationsList generate() {
        return new ViolationsList();
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _ -> this" )
    public ViolationsList generate(
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
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public ViolationsList generate (
            @lombok.NonNull final ErrorResponse errorResponse
    ) {
        return this.generate().setErrorResponse( errorResponse );
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public ViolationsList generate(
            @lombok.NonNull final String response
    ) {
        return this.generate().setViolationsInformationsList( super.stringToArrayList( response, ViolationsInformation[].class ) );
    }

    @Override
    @lombok.NonNull
    public Methods getMethodName() {
        return Methods.GET_VIOLATION_LIST;
    }

    @Override
    @lombok.NonNull
    public String getMethodApi() {
        return super.getAPI_FOR_VIOLATION_LIST();
    }

    @Override
    public void close() {
        this.getViolationsInformationsList().clear();
    }
}
