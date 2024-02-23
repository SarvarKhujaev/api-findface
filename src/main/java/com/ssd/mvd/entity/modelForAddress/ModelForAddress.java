package com.ssd.mvd.entity.modelForAddress;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.entity.modelForPassport.RequestGuid;
import com.ssd.mvd.entity.PermanentRegistration;
import com.ssd.mvd.constants.ErrorResponse;

import lombok.extern.jackson.Jacksonized;
import java.util.List;

@Jacksonized
public final class ModelForAddress {
    public com.ssd.mvd.entity.PermanentRegistration getPermanentRegistration() {
        return this.PermanentRegistration;
    }

    public void setErrorResponse( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    private RequestGuid RequestGuid;
    private PermanentRegistration PermanentRegistration;
    @JsonDeserialize
    private List< com.ssd.mvd.entity.modelForAddress.TemproaryRegistration > TemproaryRegistration;

    private ErrorResponse errorResponse;

    public static ModelForAddress generate (
            final ErrorResponse errorResponse
    ) {
        return new ModelForAddress( errorResponse );
    }

    private ModelForAddress ( final ErrorResponse errorResponse ) {
        this.setErrorResponse( errorResponse );
    }
}