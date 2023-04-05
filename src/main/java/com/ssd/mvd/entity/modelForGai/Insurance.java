package com.ssd.mvd.entity.modelForGai;

import com.ssd.mvd.constants.ErrorResponse;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class Insurance {
    private String DateBegin;
    private String DateValid;
    private String TintinType;

    private ErrorResponse errorResponse;

    public Insurance ( ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }
}
