package com.ssd.mvd.entityForLogging;

import com.ssd.mvd.controller.DataValidationInspector;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.controller.SerDes;
import com.ssd.mvd.constants.Errors;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class PersonInfo {
    private String pinfl;
    private String photo;
    private String address;
    private String cadastre;
    private String fullname;
    private String birthDate;
    private String passportNumber;

    public PersonInfo ( final PsychologyCard psychologyCard,
                        final DataValidationInspector dataValidationInspector ) {
        if ( psychologyCard.getForeignerList() == null ) {
            if ( dataValidationInspector
                    .checkObject
                    .test( psychologyCard.getPinpp() ) ) {
                this.setPinfl( psychologyCard.getPinpp().getPinpp() );
                this.setCadastre( psychologyCard.getPinpp().getCadastre() );
                this.setBirthDate( psychologyCard.getPinpp().getBirthDate() );
                this.setFullname( DataValidationInspector.getInstance().joinString.apply( psychologyCard.getPinpp() ) ); }

            this.setPassportNumber( dataValidationInspector
                    .checkData
                    .test( 7, psychologyCard.getModelForPassport() )
                    ? psychologyCard
                    .getModelForPassport()
                    .getData()
                    .getDocument()
                    .getSerialNumber()
                    : Errors.DATA_NOT_FOUND.name() );

            this.setAddress( dataValidationInspector
                    .checkData
                    .test( 8, psychologyCard.getModelForAddress() )
                    ? psychologyCard
                    .getModelForAddress()
                    .getPermanentRegistration()
                    .getPAddress()
                    : Errors.DATA_NOT_FOUND.name() );

            this.setPhoto( dataValidationInspector
                    .checkData
                    .test( 5, psychologyCard.getPapilonData() )
                    ? SerDes
                    .getSerDes()
                    .getBase64ToLink()
                    .apply( psychologyCard
                            .getPapilonData()
                            .get( 0 )
                            .getPhoto() )
                    : Errors.DATA_NOT_FOUND.name() ); }

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
                            .getPhoto() ) ); } }
}