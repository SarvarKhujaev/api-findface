package com.ssd.mvd.entity.modelForGai;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.inspectors.ErrorController;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Errors;

import java.util.List;

public final class ViolationsList
        extends ErrorController
        implements EntityCommonMethods< ViolationsList >, ServiceCommonMethods {
    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    public void setErrorResponse( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
    }

    public void setViolationsInformationsList(
            final List< ViolationsInformation > violationsInformationsList
    ) {
        this.violationsInformationsList = violationsInformationsList;
    }

    public List< ViolationsInformation > getViolationsInformationsList() {
        return this.violationsInformationsList;
    }

    private ErrorResponse errorResponse;

    @JsonDeserialize
    private List< ViolationsInformation > violationsInformationsList;

    public static ViolationsList generate (
            final List< ViolationsInformation > violationsInformationsList
    ) {
        return new ViolationsList( violationsInformationsList );
    }

    private ViolationsList (
            final List< ViolationsInformation > violationsInformationsList
    ) {
        this.setViolationsInformationsList( violationsInformationsList );
    }

    @Override
    public ViolationsList generate(
            final String message,
            final Errors errors
    ) {
        return new ViolationsList().generate(
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
        return new ViolationsList( errorResponse );
    }

    private ViolationsList (
            final ErrorResponse errorResponse
    ) {
        this.setErrorResponse( errorResponse );
    }

    public ViolationsList () {}

    @Override
    public void close() {
        this.getViolationsInformationsList().clear();
    }
}
