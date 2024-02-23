package com.ssd.mvd.entity.modelForCadastr;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.constants.ErrorResponse;
import java.util.List;

public final class Data {
    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    public void setErrorResponse ( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
    }

    public List<Person> getPermanentRegistration() {
        return this.PermanentRegistration;
    }

    private ErrorResponse errorResponse;
    @JsonDeserialize
    private List< Person > PermanentRegistration;
    @JsonDeserialize
    private List< TemproaryRegistration > TemproaryRegistration;

    public static Data generate (
            final ErrorResponse errorResponse
    ) {
        return new Data( errorResponse );
    }

    private Data ( final ErrorResponse errorResponse ) {
        this.setErrorResponse( errorResponse );
    }
}
