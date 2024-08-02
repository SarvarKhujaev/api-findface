package com.ssd.mvd.entity;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.inspectors.Config;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;

public final class Pinpp
        extends Config
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

    public Pinpp setErrorResponse ( final ErrorResponse errorResponse ) {
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
    public Pinpp generate(
            final String message,
            final Errors errors
    ) {
        return this.generate().setErrorResponse(
                super.error.apply(
                        message,
                        errors
                )
        );
    }

    @Override
    public Pinpp generate() {
        return new Pinpp();
    }

    @Override
    public Pinpp generate (
            final ErrorResponse errorResponse
    ) {
        return this.setErrorResponse( errorResponse );
    }

    @Override
    public Pinpp generate (
            final String response
    ) {
        return super.deserialize( response, this.getClass() );
    }

    @Override
    public Methods getMethodName() {
        return Methods.GET_PINPP;
    }

    @Override
    public String getMethodApi() {
        return super.getAPI_FOR_PINPP();
    }
}
