package com.ssd.mvd.entity.modelForFioOfPerson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    private String Pinpp;
    private String Cadastre;
    private String NameLatin;
    private String BirthPlace;
    private String DateOfBirth;
    private String personImage;
    private String SurnameLatin;
    private String NameCyrillic;
    private String PatronymLatin;
    private String SurnameCyrillic;
    private String PatronymCyrillic;

    @JsonDeserialize
    private Sex sex;
    @JsonDeserialize
    private Region region;
    @JsonDeserialize
    private Country country;
    @JsonDeserialize
    private Document document;
    @JsonDeserialize
    private District district;
}
