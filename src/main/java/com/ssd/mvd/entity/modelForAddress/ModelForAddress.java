package com.ssd.mvd.entity.modelForAddress;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.entity.modelForPassport.RequestGuid;
import com.ssd.mvd.entity.PermanentRegistration;
import com.ssd.mvd.constants.ErrorResponse;

import lombok.extern.jackson.Jacksonized;
import java.util.List;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class ModelForAddress {
    private RequestGuid RequestGuid;
    private PermanentRegistration PermanentRegistration;
    @JsonDeserialize
    private List< com.ssd.mvd.entity.modelForAddress.TemproaryRegistration > TemproaryRegistration;

    private ErrorResponse errorResponse;

    public ModelForAddress ( final ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }
}