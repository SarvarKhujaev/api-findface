package com.ssd.mvd.entityForLogging;

import com.ssd.mvd.inspectors.TimeInspector;

public class ErrorLog extends TimeInspector {
    private final long createdAt = super.newDate().getTime();

    private String errorMessage;

    private final String integratedService = IntegratedServiceApis.OVIR.getName();
    private final String integratedServiceApiDescription = IntegratedServiceApis.OVIR.getDescription();

    public ErrorLog (
            final String errorMessage
    ) {
        this.errorMessage = errorMessage;
    }
}