package com.ssd.mvd.entity.modelForFioOfPerson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ssd.mvd.constants.ErrorResponse;
import java.util.List;

@JsonIgnoreProperties( ignoreUnknown = true )
public final class PersonTotalDataByFIO {
    public List<Person> getData() {
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

    private Integer AnswereId;
    private String AnswereMessage;
    private String AnswereComment;
    @JsonDeserialize
    private List< Person > Data;

    private ErrorResponse errorResponse;

    public static PersonTotalDataByFIO generate ( final ErrorResponse errorResponse ) {
        return new PersonTotalDataByFIO( errorResponse );
    }

    public static PersonTotalDataByFIO generate () {
        return new PersonTotalDataByFIO();
    }

    private PersonTotalDataByFIO () {}

    private PersonTotalDataByFIO ( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
    }
}
