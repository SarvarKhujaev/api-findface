package com.ssd.mvd.entityForLogging;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorLog {
    private Long createdAt;
    private String errorMessage;
    private String integratedService;
    private String integratedServiceApiDescription;
}