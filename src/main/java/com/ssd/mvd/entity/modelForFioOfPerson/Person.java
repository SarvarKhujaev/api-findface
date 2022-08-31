package com.ssd.mvd.entity.modelForFioOfPerson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.jackson.Jacksonized;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties( ignoreUnknown = true )
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
