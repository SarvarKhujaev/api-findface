package com.ssd.mvd.entity.modelForFioOfPerson;

import lombok.extern.jackson.Jacksonized;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    private DocumentType documentType;
    private DateIssue dateIssue;
    private DateValid dateValid;

    private String SerialNumber;
    private String IssuedBy;
}
