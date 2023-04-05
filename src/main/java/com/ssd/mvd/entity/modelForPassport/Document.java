package com.ssd.mvd.entity.modelForPassport;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class Document {
    private String IssuedBy;
    private String DateIssue;
    private String SerialNumber;
    private DocumentType DocumentType;
}
