package com.ssd.mvd.entity.modelForPassport;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.constants.ErrorResponse;

import lombok.extern.jackson.Jacksonized;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Data {
    @JsonDeserialize
    private Person Person;
    @JsonDeserialize
    private Document Document;
    @JsonDeserialize
    private RequestGuid RequestGuid;

    private ErrorResponse errorResponse;

    public Data ( ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }
}
