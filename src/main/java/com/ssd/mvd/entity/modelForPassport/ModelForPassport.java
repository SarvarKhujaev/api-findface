package com.ssd.mvd.entity.modelForPassport;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.inspectors.ErrorController;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Errors;

public final class ModelForPassport
        extends ErrorController
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

    public void setErrorResponse( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
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

    @Override
    public ModelForPassport generate(
            final String message,
            final Errors errors
    ) {
        return new ModelForPassport().generate(
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
        return new ModelForPassport( errorResponse );
    }

    private ModelForPassport ( final ErrorResponse errorResponse ) {
        this.setErrorResponse( errorResponse );
    }

    public ModelForPassport () {}
}
