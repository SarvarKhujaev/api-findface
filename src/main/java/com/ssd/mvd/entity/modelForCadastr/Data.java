package com.ssd.mvd.entity.modelForCadastr;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.constants.ErrorResponse;

import lombok.extern.jackson.Jacksonized;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@lombok.Data
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Data {
    private ErrorResponse errorResponse;
    @JsonDeserialize
    private List< Person > PermanentRegistration;
    @JsonDeserialize
    private List< TemproaryRegistration > TemproaryRegistration;

    public Data ( ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }
}
