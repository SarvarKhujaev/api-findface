package com.ssd.mvd.entity.boardCrossing;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class Person {
    private byte sex;
    private byte livestatus;
    private byte transaction_id;

    private Integer citizenshipid;
    private Integer nationalityid;
    private Integer birthcountryid;

    private String namelat;
    private String engname;
    private String surnamelat;
    private String engsurname;
    private String birth_date;
    private String nationality;
    private String patronymlat;
    private String citizenship;
    private String birthcountry;
    private String current_pinpp;
    private String current_document;
}
