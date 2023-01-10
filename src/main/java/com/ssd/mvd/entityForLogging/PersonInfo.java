package com.ssd.mvd.entityForLogging;

import com.ssd.mvd.entity.modelForFioOfPerson.PersonTotalDataByFIO;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.constants.Errors;

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

    public PersonInfo ( PsychologyCard psychologyCard, String image ) {
        if ( psychologyCard.getForeignerList() == null ) {
            this.setPhoto( image );
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
                            .getPatronym() )
                    : Errors.DATA_NOT_FOUND.name() );
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
                    : Errors.DATA_NOT_FOUND.name() ); }
        else {
            this.setPhoto( image );
            this.setPassportNumber ( psychologyCard
                    .getPapilonData()
                    .get( 0 )
                    .getPassport() );

            this.setPinfl( psychologyCard
                    .getPapilonData()
                    .get( 0 )
                    .getPersonal_code() ); } }

    public PersonInfo ( PersonTotalDataByFIO personTotalDataByFIO, String image ) {
        if ( personTotalDataByFIO.getData() != null
                && !personTotalDataByFIO.getData().isEmpty() ) {
            this.setPhoto( image );
            this.setPinfl( personTotalDataByFIO.getData().get( 0 ).getPinpp() );
            this.setCadastre( personTotalDataByFIO.getData().get( 0 ).getCadastre() );
            this.setAddress( personTotalDataByFIO.getData().get( 0 ).getBirthPlace() );
            this.setFullname( personTotalDataByFIO.getData().get( 0 ).getNameLatin()
                    + " "
                    + personTotalDataByFIO.getData().get( 0 ).getSurnameLatin()
                    + " "
                    + personTotalDataByFIO.getData().get( 0 ).getPatronymLatin() );
            this.setBirthDate( personTotalDataByFIO.getData().get( 0 ).getDateOfBirth() ); } }
}
