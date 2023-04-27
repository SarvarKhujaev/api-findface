package com.ssd.mvd.entityForLogging;

import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.controller.SerDes;
import com.ssd.mvd.constants.Errors;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class PersonInfo {
    private String pinfl;
    private String photo;
    private String address;
    private String cadastre;
    private String fullname;
    private String birthDate;
    private String passportNumber;

    public PersonInfo ( final PsychologyCard psychologyCard ) {
        if ( psychologyCard.getForeignerList() == null ) {
            this.setFullname( psychologyCard.getPinpp() != null ?
                    ( psychologyCard
                            .getPinpp()
                            .getName()
                            + " "
                            + psychologyCard
                            .getPinpp()
                            .getSurname()
                            + " "
                            + psychologyCard
                            .getPinpp()
                            .getPatronym() ) :
                    Errors.DATA_NOT_FOUND.name());
            this.setPinfl( psychologyCard
                    .getPinpp() != null
                    ? psychologyCard
                    .getPinpp()
                    .getPinpp()
                    : Errors.DATA_NOT_FOUND.name() );
            this.setBirthDate( psychologyCard
                    .getPinpp() != null
                    ? psychologyCard
                    .getPinpp()
                    .getBirthDate()
                    : Errors.DATA_NOT_FOUND.name() );
            this.setPassportNumber( psychologyCard
                    .getModelForPassport() != null
                    && psychologyCard
                    .getModelForPassport()
                    .getData()
                    .getDocument() != null
                    ? psychologyCard
                    .getModelForPassport()
                    .getData()
                    .getDocument()
                    .getSerialNumber()
                    : Errors.DATA_NOT_FOUND.name() );
            this.setAddress( psychologyCard
                    .getModelForAddress() != null
                    && psychologyCard
                    .getModelForAddress()
                    .getPermanentRegistration() != null
                    ? psychologyCard
                    .getModelForAddress()
                    .getPermanentRegistration()
                    .getPAddress()
                    : Errors.DATA_NOT_FOUND.name() );
            this.setCadastre( psychologyCard
                    .getPinpp() != null
                    ? psychologyCard
                    .getPinpp()
                    .getCadastre()
                    : Errors.DATA_NOT_FOUND.name() );
            if ( psychologyCard.getPapilonData() != null
                    && psychologyCard.getPapilonData().size() > 0 ) this.setPhoto( SerDes
                    .getSerDes()
                    .getBase64ToLink()
                    .apply( psychologyCard
                            .getPapilonData()
                            .get( 0 )
                            .getPhoto() ) ); }

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