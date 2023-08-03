package com.ssd.mvd.entity.modelForFioOfPerson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ssd.mvd.constants.ErrorResponse;
import java.util.List;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@JsonIgnoreProperties( ignoreUnknown = true )
public final class PersonTotalDataByFIO {
    private Integer AnswereId;
    private String AnswereMessage;
    private String AnswereComment;
    @JsonDeserialize
    private List< Person > Data;

    private ErrorResponse errorResponse;

    public PersonTotalDataByFIO ( final ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }
}
