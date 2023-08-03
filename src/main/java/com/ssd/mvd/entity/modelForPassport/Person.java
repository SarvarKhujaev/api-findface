package com.ssd.mvd.entity.modelForPassport;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class Person {
    private Sex Sex;
    private String Pinpp;
    private String pCitizen;
    private String NameLatin;
    private String DateBirth;
    private String BirthPlace;
    private String SurnameLatin;
    private String PatronymLatin;
}
