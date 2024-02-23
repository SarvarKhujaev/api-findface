package com.ssd.mvd.entityForLogging;

import com.ssd.mvd.controller.DataValidationInspector;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.controller.SerDes;
import com.ssd.mvd.constants.Errors;

public final class PersonInfo extends DataValidationInspector {
    public void setPinfl ( final String pinfl ) {
        this.pinfl = pinfl;
    }

    public void setPhoto ( final String photo ) {
        this.photo = photo;
    }

    public void setAddress ( final String address ) {
        this.address = address;
    }

    public void setCadastre ( final String cadastre ) {
        this.cadastre = cadastre;
    }

    public void setFullname ( final String fullname ) {
        this.fullname = fullname;
    }

    public void setBirthDate ( final String birthDate ) {
        this.birthDate = birthDate;
    }

    public void setPassportNumber ( final String passportNumber ) {
        this.passportNumber = passportNumber;
    }

    private String pinfl;
    private String photo;
    private String address;
    private String cadastre;
    private String fullname;
    private String birthDate;
    private String passportNumber;

    public PersonInfo ( final PsychologyCard psychologyCard ) {
        if ( psychologyCard.getForeignerList() == null ) {
            if ( super.checkObject( psychologyCard.getPinpp() ) ) {
                this.setPinfl( psychologyCard.getPinpp().getPinpp() );
                this.setCadastre( psychologyCard.getPinpp().getCadastre() );
                this.setBirthDate( psychologyCard.getPinpp().getBirthDate() );
                this.setFullname( super.joinString( psychologyCard.getPinpp() ) );
            }

            this.setPassportNumber( super.checkPassport( psychologyCard.getModelForPassport() )
                    ? psychologyCard
                    .getModelForPassport()
                    .getData()
                    .getDocument()
                    .getSerialNumber()
                    : Errors.DATA_NOT_FOUND.name() );

            this.setAddress( super.check( psychologyCard.getModelForAddress() )
                    ? psychologyCard
                    .getModelForAddress()
                    .getPermanentRegistration()
                    .getPAddress()
                    : Errors.DATA_NOT_FOUND.name() );

            this.setPhoto( super.check( psychologyCard.getPapilonData() )
                    ? SerDes
                    .getSerDes()
                    .getBase64ToLink()
                    .apply( psychologyCard
                            .getPapilonData()
                            .get( 0 )
                            .getPhoto() )
                    : Errors.DATA_NOT_FOUND.name() );
        }

        else {
            this.setPassportNumber ( psychologyCard
                    .getPapilonData()
                    .get( 0 )
                    .getPassport() );

            this.setPinfl( psychologyCard
                    .getPapilonData()
                    .get( 0 )
                    .getPersonal_code() );

            this.setPhoto( SerDes
                    .getSerDes()
                    .getBase64ToLink()
                    .apply( psychologyCard
                            .getPapilonData()
                            .get( 0 )
                            .getPhoto() ) );
        } }
}