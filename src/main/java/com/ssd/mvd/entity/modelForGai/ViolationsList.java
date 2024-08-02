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

    private ViolationsList setErrorResponse( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

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
    public ViolationsList generate() {
        return new ViolationsList();
    }

    @Override
    public ViolationsList generate(
            final String message,
            final Errors errors
    ) {
        return this.generate().setErrorResponse(
                super.error.apply(
                        message,
                        errors
                )
        );
    }

    @Override
    public ViolationsList generate (
            final ErrorResponse errorResponse
    ) {
        return this.generate().setErrorResponse( errorResponse );
    }

    @Override
    public ViolationsList generate(
            final String response
    ) {
        return this.generate().setViolationsInformationsList( super.stringToArrayList( response, ViolationsInformation[].class ) );
    }

    @Override
    public Methods getMethodName() {
        return Methods.GET_VIOLATION_LIST;
    }

    @Override
    public String getMethodApi() {
        return super.getAPI_FOR_VIOLATION_LIST();
    }

    @Override
    public void close() {
        this.getViolationsInformationsList().clear();
    }
}
