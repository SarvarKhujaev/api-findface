package com.ssd.mvd.entity;

import com.ssd.mvd.entity.modelForFioOfPerson.PersonTotalDataByFIO;
import com.ssd.mvd.controller.SerDes;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonInfo {
    private String pinfl;
    private String photo;
    private String address;
    private String cadastre;
    private String fullname;
    private String birthDate;
    private String passportNumber;

    public PersonInfo ( PsychologyCard psychologyCard ) {
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
                            .getPatronym() ) : "unknown" );
            this.setPinfl( psychologyCard
                    .getPinpp() != null
                    ? psychologyCard
                    .getPinpp()
                    .getPinpp() : "unknown" );
            this.setBirthDate( psychologyCard
                    .getPinpp() != null
                    ? psychologyCard
                    .getPinpp()
                    .getBirthDate() : "unknown" );
            this.setPassportNumber( psychologyCard
                    .getModelForPassport() != null
                    && psychologyCard
                    .getModelForPassport()
                    .getDocument() != null
                    ? psychologyCard
                    .getModelForPassport()
                    .getDocument()
                    .getSerialNumber() : "unknown" );
            this.setAddress( psychologyCard
                    .getModelForAddress() != null
                    && psychologyCard
                    .getModelForAddress()
                    .getPermanentRegistration() != null
                    ? psychologyCard
                    .getModelForAddress()
                    .getPermanentRegistration()
                    .getPAddress() : "unknown" );
            this.setCadastre( psychologyCard
                    .getPinpp() != null
                    ? psychologyCard
                    .getPinpp()
                    .getCadastre() : "unknown" );
            this.setPhoto( psychologyCard
                    .getPersonImage() != null
                    ? psychologyCard
                    .getPersonImage() : "not found" ); }

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
                    .base64ToLink( psychologyCard
                            .getPapilonData()
                            .get( 0 )
                            .getPhoto() ) ); } }

    public PersonInfo ( PersonTotalDataByFIO personTotalDataByFIO ) {
        if ( personTotalDataByFIO.getData() != null
                && !personTotalDataByFIO.getData().isEmpty()
                && personTotalDataByFIO.getData().size() > 0 ) {
            this.setPinfl( personTotalDataByFIO.getData().get( 0 ).getPinpp() );
            this.setCadastre( personTotalDataByFIO.getData().get( 0 ).getCadastre() );
            this.setAddress( personTotalDataByFIO.getData().get( 0 ).getBirthPlace() );
            this.setPhoto( SerDes
                    .getSerDes()
                    .base64ToLink( personTotalDataByFIO.getData().get( 0 ).getPersonImage() ) );
            this.setFullname( personTotalDataByFIO.getData().get( 0 ).getNameLatin()
                    + " "
                    + personTotalDataByFIO.getData().get( 0 ).getSurnameLatin()
                    + " "
                    + personTotalDataByFIO.getData().get( 0 ).getPatronymLatin() );
            this.setBirthDate( personTotalDataByFIO.getData().get( 0 ).getDateOfBirth() ); } }
}