package com.ssd.mvd.constants;

@lombok.Data
@lombok.Builder
public class ErrorResponse {
    private String message;
    private Errors errors;
}
