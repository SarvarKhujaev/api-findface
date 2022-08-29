package com.ssd.mvd.entity.modelForFioOfPerson;

import lombok.extern.jackson.Jacksonized;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Country {
    private Integer Id;
    private String Value;
}
