package com.ssd.mvd.entity.modelForPassport;

import com.ssd.mvd.constants.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class Data {
    private Person Person;
    private Document Document;
    private RequestGuid RequestGuid;

    private ErrorResponse errorResponse;

    public Data ( ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }
}
