package com.ssd.mvd.entity.modelForPassport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    private String IssuedBy;
    private String DateIssue;
    private String SerialNumber;
    private DocumentType DocumentType;
}
