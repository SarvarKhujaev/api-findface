package com.ssd.mvd.entity.modelForFioOfPerson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.inspectors.ErrorController;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;

import java.util.List;

@JsonIgnoreProperties( ignoreUnknown = true )
public final class PersonTotalDataByFIO
        extends ErrorController
        implements EntityCommonMethods< PersonTotalDataByFIO > {
    public List< Person > getData() {
        return this.Data;
    }

    public void setData( final List< Person > data ) {
        this.Data = data;
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    public void setErrorResponse( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
    }

    public int getAnswereId() {
        return AnswereId;
    }

    public String getAnswereMessage() {
        return AnswereMessage;
    }

    public String getAnswereComment() {
        return AnswereComment;
    }

    private int AnswereId;
    private String AnswereMessage;
    private String AnswereComment;
    @JsonDeserialize
    private List< Person > Data;

    private ErrorResponse errorResponse;

    public PersonTotalDataByFIO () {}

    private PersonTotalDataByFIO ( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
    }

    @Override
    public PersonTotalDataByFIO generate() {
        return new PersonTotalDataByFIO();
    }

    @Override
    public PersonTotalDataByFIO generate (
            final ErrorResponse errorResponse
    ) {
        return new PersonTotalDataByFIO( errorResponse );
    }

    @Override
    public PersonTotalDataByFIO generate(
            final String message,
            final Errors errors
    ) {
        return new PersonTotalDataByFIO(
                super.error.apply(
                        message,
                        errors
                )
        );
    }

    @Override
    public Methods getMethodName() {
        return Methods.GET_PERSON_TOTAL_DATA_BY_FIO;
    }
}
