package com.ssd.mvd.entity.modelForGai;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.constants.ErrorResponse;

import lombok.extern.jackson.Jacksonized;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;
import lombok.Data;

@Data
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ViolationsList {
    @JsonDeserialize
    private List< ViolationsInformation > violationsInformationsList;

    private ErrorResponse errorResponse;

    public ViolationsList( ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }

    public ViolationsList( List< ViolationsInformation > violationsInformationsList ) { this.setViolationsInformationsList( violationsInformationsList ); }
}
