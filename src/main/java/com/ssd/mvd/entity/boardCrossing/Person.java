package com.ssd.mvd.entity.boardCrossing;

import com.google.gson.annotations.Expose;

import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.inspectors.CustomSerializer;
import com.ssd.mvd.interfaces.EntityCommonMethods;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public final class Person implements EntityCommonMethods< Person > {
    public int getNationalityid() {
        return this.nationalityid;
    }

    public byte getSex() {
        return sex;
    }

    public byte getLivestatus() {
        return livestatus;
    }

    public byte getTransaction_id() {
        return transaction_id;
    }

    public int getCitizenshipid() {
        return citizenshipid;
    }

    public int getBirthcountryid() {
        return birthcountryid;
    }

    public String getNamelat() {
        return namelat;
    }

    public String getEngname() {
        return engname;
    }

    public String getSurnamelat() {
        return surnamelat;
    }

    public String getEngsurname() {
        return engsurname;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public String getNationality() {
        return nationality;
    }

    public String getPatronymlat() {
        return patronymlat;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public String getBirthcountry() {
        return birthcountry;
    }

    public String getCurrent_pinpp() {
        return current_pinpp;
    }

    public String getCurrent_document() {
        return current_document;
    }

    @Expose
    private byte sex;
    @Expose
    private byte livestatus;
    @Expose
    private byte transaction_id;

    @Expose
    private int citizenshipid;
    @Expose
    private int nationalityid;
    @Expose
    private int birthcountryid;

    @Expose
    private String namelat;
    @Expose
    private String engname;
    @Expose
    private String surnamelat;
    @Expose
    private String engsurname;
    @Expose
    private String birth_date;
    @Expose
    private String nationality;
    @Expose
    private String patronymlat;
    @Expose
    private String citizenship;
    @Expose
    private String birthcountry;
    @Expose
    private String current_pinpp;
    @Expose
    private String current_document;

    public Person () {}

    @Override
    @lombok.NonNull
    public Person generate() {
        return new Person();
    }

    @Override
    public @lombok.NonNull Person setErrorResponse( @lombok.NonNull final ErrorResponse errorResponse ) {
        return this.generate();
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public Person generate( @lombok.NonNull final String response ) {
        return CustomSerializer.deserialize(
                response.substring( response.indexOf( "transaction_id" ) - 2, response.indexOf( "sex" ) + 9 ),
                this.getClass()
        );
    }
}
