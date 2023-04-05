package com.ssd.mvd.entity.modelForFioOfPerson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.extern.jackson.Jacksonized;

@lombok.Data
@Jacksonized
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@JsonIgnoreProperties( ignoreUnknown = true )
public class Document {
    @JsonDeserialize
    private DocumentType DocumentType;
    @JsonDeserialize
    private DateIssue DateIssue;
    @JsonDeserialize
    private DateValid DateValid;

    private String SerialNumber;
    private String IssuedBy;
}
