package com.ssd.mvd.entity.modelForPassport;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.constants.ErrorResponse;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class ModelForPassport {
    private Integer AnswereId;
    private String AnswereMessage;
    private String AnswereComment;
    @JsonDeserialize
    private com.ssd.mvd.entity.modelForPassport.Data Data;

    private ErrorResponse errorResponse;

    public ModelForPassport ( final ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }
}
