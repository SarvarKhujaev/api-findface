package com.ssd.mvd.entity.modelForAddress;

import com.ssd.mvd.entity.modelForPassport.RequestGuid;
import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.entity.PermanentRegistration;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.inspectors.Config;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.extern.jackson.Jacksonized;
import java.util.List;

@Jacksonized
public final class ModelForAddress
        extends Config
        implements EntityCommonMethods< ModelForAddress >, ServiceCommonMethods {
    public com.ssd.mvd.entity.PermanentRegistration getPermanentRegistration() {
        return this.PermanentRegistration;
    }

    public ModelForAddress setErrorResponse( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
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

    public ModelForAddress () {}

    @Override
    public Methods getMethodName () {
        return Methods.GET_MODEL_FOR_ADDRESS;
    }

    @Override
    public String getMethodApi() {
        return super.getAPI_FOR_MODEL_FOR_ADDRESS();
    }

    @Override
    public ModelForAddress generate(
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
    public ModelForAddress generate (
            final ErrorResponse errorResponse
    ) {
        return this.setErrorResponse( errorResponse );
    }

    @Override
    public ModelForAddress generate (
            final String response
    ) {
        return super.deserialize(
                response.substring( response.indexOf( "Data" ) + 6, response.indexOf( ",\"AnswereId" ) ),
                ModelForAddress.class
        );
    }

    @Override
    public ModelForAddress generate () {
        return new ModelForAddress();
    }

    @Override
    public void close() {
        this.getTemproaryRegistration().clear();
    }
}