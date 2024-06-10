package com.ssd.mvd.entity;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.controller.ErrorController;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Errors;

public final class Pinpp
        extends ErrorController
        implements EntityCommonMethods< Pinpp > {
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

    public void setErrorResponse ( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
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

    @Override
    public Pinpp generate(
            final String message,
            final Errors errors
    ) {
        return new Pinpp().generate(
                super.error.apply(
                        message,
                        errors
                )
        );
    }

    @Override
    public Pinpp generate (
            final ErrorResponse errorResponse
    ) {
        return new Pinpp( errorResponse );
    }

    private Pinpp ( final ErrorResponse errorResponse ) {
        this.setErrorResponse( errorResponse );
    }

    public Pinpp () {}
}
