package com.ssd.mvd.entity;

import com.ssd.mvd.entity.modelForFioOfPerson.PersonTotalDataByFIO;
import com.ssd.mvd.entity.modelForFioOfPerson.FIO;

import java.util.Date;
import lombok.Data;

@Data
public class UserRequest {
    private Date createdAt;
    private PersonInfo personInfo;

    private String userPassportNumber;
    private String integratedServiceName;
    private final String microserviceName = "api-findface";

    public UserRequest ( PersonTotalDataByFIO personTotalDataByFIO, FIO fio ) {
        this.setCreatedAt( new Date() );
        this.setPersonInfo( new PersonInfo( personTotalDataByFIO ) );

        this.setIntegratedServiceName( "ZAKS" );
        this.setUserPassportNumber( fio.getUser().getPassportNumber() ); }

    public UserRequest ( PsychologyCard psychologyCard, ApiResponseModel apiResponseModel ) {
        this.setCreatedAt( new Date() );
        this.setPersonInfo( new PersonInfo( psychologyCard ) );

        this.setIntegratedServiceName( "OVIR" );
        this.setUserPassportNumber( apiResponseModel.getUser().getPassportNumber() ); }
}
