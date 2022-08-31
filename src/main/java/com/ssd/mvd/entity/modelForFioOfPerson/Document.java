package com.ssd.mvd.entity.modelForFioOfPerson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.extern.jackson.Jacksonized;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties( ignoreUnknown = true )
public class Document {
    @JsonDeserialize
    private DocumentType documentType;
    @JsonDeserialize
    private DateIssue dateIssue;
    @JsonDeserialize
    private DateValid dateValid;

    private String SerialNumber;
    private String IssuedBy;
}
