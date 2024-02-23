package com.ssd.mvd.entity.modelForGai;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.constants.ErrorResponse;
import java.util.List;

public final class ViolationsList {
    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    public void setErrorResponse( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
    }

    private ErrorResponse errorResponse;

    public void setViolationsInformationsList(
            final List< ViolationsInformation > violationsInformationsList
    ) {
        this.violationsInformationsList = violationsInformationsList;
    }

    @JsonDeserialize
    private List< ViolationsInformation > violationsInformationsList;

    public static ViolationsList generate (
            final List< ViolationsInformation > violationsInformationsList
    ) {
        return new ViolationsList( violationsInformationsList );
    }

    public static ViolationsList generate (
            final ErrorResponse errorResponse
    ) {
        return new ViolationsList( errorResponse );
    }

    private ViolationsList ( final ErrorResponse errorResponse ) {
        this.setErrorResponse( errorResponse );
    }

    private ViolationsList (
            final List< ViolationsInformation > violationsInformationsList
    ) {
        this.setViolationsInformationsList( violationsInformationsList );
    }
}
