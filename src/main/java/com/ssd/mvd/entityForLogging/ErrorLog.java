package com.ssd.mvd.entityForLogging;

@lombok.Data
@lombok.Builder
public class ErrorLog {
    private Long createdAt;
    private String errorMessage;
    private String integratedService;
    private String integratedServiceApiDescription;
}