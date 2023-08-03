package com.ssd.mvd.entity.modelForGai;

import com.ssd.mvd.constants.ErrorResponse;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class Insurance {
    private String DateBegin;
    private String DateValid;
    private String TintinType;

    private ErrorResponse errorResponse;

    public Insurance ( final ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }
}
