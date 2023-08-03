package com.ssd.mvd.entity.modelForPassport;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.constants.ErrorResponse;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class Data {
    @JsonDeserialize
    private Person Person;
    @JsonDeserialize
    private Document Document;
    @JsonDeserialize
    private RequestGuid RequestGuid;

    private ErrorResponse errorResponse;
}
