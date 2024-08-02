package com.ssd.mvd.entity.modelForPassport;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.inspectors.Config;
import com.ssd.mvd.constants.Errors;

public final class ModelForPassport
        extends Config
        implements EntityCommonMethods< ModelForPassport > {
    public com.ssd.mvd.entity.modelForPassport.Data getData () {
        return this.Data;
    }

    public void setData( final com.ssd.mvd.entity.modelForPassport.Data data ) {
        this.Data = data;
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    public ModelForPassport setErrorResponse( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    public Integer getAnswereId() {
        return this.AnswereId;
    }

    public String getAnswereMessage() {
        return this.AnswereMessage;
    }

    public String getAnswereComment() {
        return this.AnswereComment;
    }

    private int AnswereId;
    private String AnswereMessage;
    private String AnswereComment;
    @JsonDeserialize
    private com.ssd.mvd.entity.modelForPassport.Data Data;

    private ErrorResponse errorResponse;

    public ModelForPassport () {}

    @Override
    public ModelForPassport generate(
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
    public ModelForPassport generate (
            final ErrorResponse errorResponse
    ) {
        return this.generate().setErrorResponse( errorResponse );
    }

    @Override
    public ModelForPassport generate() {
        return new ModelForPassport();
    }

    @Override
    public Methods getMethodName() {
        return Methods.GET_MODEL_FOR_PASSPORT;
    }

    @Override
    public String getMethodApi() {
        return super.getAPI_FOR_PASSPORT_MODEL();
    }

    @Override
    public ModelForPassport generate(
            final String response
    ) {
        return super.deserialize( response, this.getClass() );
    }
}
