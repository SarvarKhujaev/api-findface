package com.ssd.mvd.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.entity.modelForGai.ModelForCar;
import com.ssd.mvd.constants.ErrorResponse;

import lombok.extern.jackson.Jacksonized;
import java.util.List;

@lombok.Data
@Jacksonized
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class ModelForCarList {
    private ErrorResponse errorResponse;
    @JsonDeserialize
    private List< ModelForCar > modelForCarList;

    public ModelForCarList ( final ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }

    public ModelForCarList ( final List< ModelForCar > modelForCarList ) { this.setModelForCarList( modelForCarList ); }
}
