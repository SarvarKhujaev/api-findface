package com.ssd.mvd.entity.boardCrossing;

public final class Person {
    public byte getSex() {
        return this.sex;
    }

    public byte getLivestatus() {
        return this.livestatus;
    }

    public int getNationalityid() {
        return this.nationalityid;
    }

    public byte getTransaction_id() {
        return this.transaction_id;
    }

    public int getCitizenshipid() {
        return this.citizenshipid;
    }

    public int getBirthcountryid() {
        return this.birthcountryid;
    }

    public String getNamelat() {
        return this.namelat;
    }

    public String getEngname() {
        return this.engname;
    }

    public String getSurnamelat() {
        return this.surnamelat;
    }

    public String getEngsurname() {
        return this.engsurname;
    }

    public String getBirth_date() {
        return this.birth_date;
    }

    public String getNationality() {
        return this.nationality;
    }

    public String getPatronymlat() {
        return this.patronymlat;
    }

    public String getCitizenship() {
        return this.citizenship;
    }

    public String getBirthcountry() {
        return this.birthcountry;
    }

    public String getCurrent_pinpp() {
        return this.current_pinpp;
    }

    public String getCurrent_document() {
        return this.current_document;
    }

    private byte sex;
    private byte livestatus;
    private byte transaction_id;

    private int citizenshipid;
    private int nationalityid;
    private int birthcountryid;

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

    public Person () {}
}
