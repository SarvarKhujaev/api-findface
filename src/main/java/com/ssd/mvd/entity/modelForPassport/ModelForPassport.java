package com.ssd.mvd.entity.modelForPassport;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.constants.ErrorResponse;

public final class ModelForPassport {
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

    private Integer AnswereId;
    private String AnswereMessage;
    private String AnswereComment;
    @JsonDeserialize
    private com.ssd.mvd.entity.modelForPassport.Data Data;

    private ErrorResponse errorResponse;

    public static ModelForPassport generate (
            final ErrorResponse errorResponse
    ) {
        return new ModelForPassport( errorResponse );
    }

    private ModelForPassport ( final ErrorResponse errorResponse ) {
        this.setErrorResponse( errorResponse );
    }
}
