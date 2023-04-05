package com.ssd.mvd.entity.modelForFioOfPerson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.jackson.Jacksonized;

@lombok.Data
@Jacksonized
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@JsonIgnoreProperties( ignoreUnknown = true )
public class District {
    private Integer Id;
    private String Value;
}
