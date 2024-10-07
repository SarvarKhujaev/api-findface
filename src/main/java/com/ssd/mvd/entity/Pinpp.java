package com.ssd.mvd.entity;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.inspectors.CustomSerializer;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;

public final class Pinpp implements EntityCommonMethods< Pinpp > {
    public String getName() {
        return this.Name;
    }

    public void setName ( final String name ) {
        Name = name;
    }

    public String getPinpp() {
        return this.Pinpp;
    }

    public void setPinpp ( final String pinpp ) {
        Pinpp = pinpp;
    }

    public String getSurname() {
        return this.Surname;
    }

    public String getPatronym() {
        return this.Patronym;
    }

    public String getCadastre() {
        return this.Cadastre;
    }

    public String getBirthDate() {
        return this.BirthDate;
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public Pinpp setErrorResponse ( @lombok.NonNull final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    private String Name;
    private String Pinpp;
    private String Region;
    private String Surname;
    private String Country;
    private String Address;
    private String Passport;
    private String pCitizen;
    private String District;
    private String Patronym;
    private String Cadastre;
    private String BirthDate;
    private String BirthPlace;
    private String BirthPlaceRegion;
    private String BirthPlaceCountry;
    private String BirthPlaceDistrict;

    private ErrorResponse errorResponse;

    public Pinpp () {}

    @Override
    @lombok.NonNull
    public Pinpp generate() {
        return new Pinpp();
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public Pinpp generate (
            @lombok.NonNull final String response
    ) {
        return CustomSerializer.deserialize( response, this.getClass() );
    }

    @Override
    @lombok.NonNull
    public Methods getMethodName() {
        return Methods.GET_PINPP;
    }
}
