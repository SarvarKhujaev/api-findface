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

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public ModelForPassport setErrorResponse( @lombok.NonNull final ErrorResponse errorResponse ) {
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
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _ -> this" )
    public ModelForPassport generate(
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
    public ModelForPassport generate (
            @lombok.NonNull final ErrorResponse errorResponse
    ) {
        return this.generate().setErrorResponse( errorResponse );
    }

    @Override
    @lombok.NonNull
    public ModelForPassport generate() {
        return new ModelForPassport();
    }

    @Override
    @lombok.NonNull
    public Methods getMethodName() {
        return Methods.GET_MODEL_FOR_PASSPORT;
    }

    @Override
    @lombok.NonNull
    public String getMethodApi() {
        return super.getAPI_FOR_PASSPORT_MODEL();
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public ModelForPassport generate(
            @lombok.NonNull final String response
    ) {
        return super.deserialize( response, this.getClass() );
    }
}
