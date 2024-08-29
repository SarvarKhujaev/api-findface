package com.ssd.mvd.constants;

@lombok.Data
@lombok.Builder
public final class ErrorResponse {
    private String message;
    private Errors errors;
}
