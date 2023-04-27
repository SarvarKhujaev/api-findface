package com.ssd.mvd.entity.modelForGai;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.constants.ErrorResponse;

import lombok.extern.jackson.Jacksonized;
import java.util.List;

@lombok.Data
@Jacksonized
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class ViolationsList {
    @JsonDeserialize
    private List< ViolationsInformation > violationsInformationsList;

    private ErrorResponse errorResponse;

    public ViolationsList ( final ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }

    public ViolationsList ( final List< ViolationsInformation > violationsInformationsList ) { this.setViolationsInformationsList( violationsInformationsList ); }
}
