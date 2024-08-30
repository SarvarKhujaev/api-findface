package com.ssd.mvd.entityForLogging;

import com.google.gson.annotations.Expose;

import com.ssd.mvd.inspectors.DataValidationInspector;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.inspectors.SerDes;
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

    @Expose
    private String pinfl;
    @Expose
    private String photo;
    @Expose
    private String address;
    @Expose
    private String cadastre;
    @Expose
    private String fullname;
    @Expose
    private String birthDate;
    @Expose
    private String passportNumber;

    public PersonInfo ( @lombok.NonNull final PsychologyCard psychologyCard ) {
        if ( !super.isCollectionNotEmpty( psychologyCard.getForeignerList() ) ) {
            if ( super.objectIsNotNull( psychologyCard.getPinpp() ) ) {
                this.setPinfl( psychologyCard.getPinpp().getPinpp() );
                this.setCadastre( psychologyCard.getPinpp().getCadastre() );
                this.setBirthDate( psychologyCard.getPinpp().getBirthDate() );
                this.setFullname( super.joinString( psychologyCard.getPinpp() ) );
            }

            this.setPassportNumber(
                    super.checkPassport( psychologyCard.getModelForPassport() )
                            ? psychologyCard
                            .getModelForPassport()
                            .getData()
                            .getDocument()
                            .getSerialNumber()
                            : Errors.DATA_NOT_FOUND.name()
            );

            this.setAddress(
                    super.check( psychologyCard.getModelForAddress() )
                            ? psychologyCard
                                .getModelForAddress()
                                .getPermanentRegistration()
                                .getPAddress()
                            : Errors.DATA_NOT_FOUND.name()
            );

            this.setPhoto(
                    super.isCollectionNotEmpty( psychologyCard.getPapilonData() )
                            ? SerDes
                            .getSerDes()
                            .getBase64ToLink()
                            .apply( psychologyCard
                                    .getPapilonData()
                                    .get( 0 )
                                    .getPhoto() )
                            : Errors.DATA_NOT_FOUND.name()
            );
        }

        else {
            this.setPassportNumber(
                    psychologyCard
                        .getPapilonData()
                        .get( 0 )
                        .getPassport()
            );

            this.setPinfl(
                    psychologyCard
                        .getPapilonData()
                        .get( 0 )
                        .getPersonal_code()
            );

            this.setPhoto(
                    SerDes
                        .getSerDes()
                        .getBase64ToLink()
                        .apply(
                                psychologyCard
                                    .getPapilonData()
                                    .get( 0 )
                                    .getPhoto()
                        )
            );
        } }

    @Override
    @lombok.NonNull
    public String toString() {
        return String.join(
                SPACE,
                super.checkString( this.pinfl ),
                super.checkString( this.photo ),
                super.checkString( this.address ),
                super.checkString( this.cadastre ),
                super.checkString( this.fullname ),
                super.checkString( this.birthDate ),
                super.checkString( this.passportNumber )
        );
    }
}