package com.ssd.mvd.entity.boardCrossing;

import com.google.gson.annotations.Expose;

import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.inspectors.CustomSerializer;
import com.ssd.mvd.interfaces.EntityCommonMethods;

public final class Person extends CustomSerializer implements EntityCommonMethods< Person > {
    public int getNationalityid() {
        return this.nationalityid;
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
    public Person generate(
            final ErrorResponse errorResponse
    ) {
        return null;
    }

    @Override
    public Person generate(
            final String message,
            final Errors errors
    ) {
        return null;
    }

    @Override
    public Person generate() {
        return new Person();
    }

    @Override
    public Person generate( final String response ) {
        return super.deserialize(
                response.substring( response.indexOf( "transaction_id" ) - 2, response.indexOf( "sex" ) + 9 ),
                this.getClass()
        );
    }
}
