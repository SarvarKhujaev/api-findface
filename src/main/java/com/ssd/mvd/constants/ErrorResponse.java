package com.ssd.mvd.constants;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private String message;
    private Errors errors;

    public ErrorResponse( String message, Errors errors ) {
        this.setMessage( message );
        this.setErrors( errors ); }
}
