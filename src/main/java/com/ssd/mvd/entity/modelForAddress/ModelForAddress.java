package com.ssd.mvd.entity.modelForAddress;

import com.ssd.mvd.entity.modelForPassport.RequestGuid;
import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.entity.PermanentRegistration;
import com.ssd.mvd.controller.ErrorController;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Errors;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.extern.jackson.Jacksonized;
import java.util.List;

@Jacksonized
public final class ModelForAddress
        extends ErrorController
        implements EntityCommonMethods< ModelForAddress >, ServiceCommonMethods {
    public com.ssd.mvd.entity.PermanentRegistration getPermanentRegistration() {
        return this.PermanentRegistration;
    }

    public void setErrorResponse( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    public List< com.ssd.mvd.entity.modelForAddress.TemproaryRegistration > getTemproaryRegistration() {
        return this.TemproaryRegistration;
    }

    private RequestGuid RequestGuid;
    private PermanentRegistration PermanentRegistration;
    @JsonDeserialize
    private List< com.ssd.mvd.entity.modelForAddress.TemproaryRegistration > TemproaryRegistration;

    private ErrorResponse errorResponse;

    @Override
    public ModelForAddress generate(
            final String message,
            final Errors errors
    ) {
        return new ModelForAddress().generate(
                super.error.apply(
                        message,
                        errors
                )
        );
    }

    @Override
    public ModelForAddress generate (
            final ErrorResponse errorResponse
    ) {
        return new ModelForAddress( errorResponse );
    }

    private ModelForAddress ( final ErrorResponse errorResponse ) {
        this.setErrorResponse( errorResponse );
    }

    public ModelForAddress () {}

    @Override
    public void close() {
        this.getTemproaryRegistration().clear();
    }
}