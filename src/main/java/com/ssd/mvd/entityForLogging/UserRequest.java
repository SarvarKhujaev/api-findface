package com.ssd.mvd.entityForLogging;

import com.ssd.mvd.entity.ApiResponseModel;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.constants.Errors;

import java.util.Date;
import lombok.Data;

@Data
public class UserRequest {
    private Long createdAt;
    private PersonInfo personInfo;

    private String userPassportNumber;
    private String integratedServiceName;
    private final String microserviceName = "api-findface";

    public UserRequest ( PsychologyCard psychologyCard, ApiResponseModel apiResponseModel ) {
        this.setCreatedAt( new Date().getTime() );
        this.setPersonInfo( new PersonInfo( psychologyCard ) );

        this.setIntegratedServiceName( "OVIR" );
        this.setUserPassportNumber( apiResponseModel.getUser() != null
                ? apiResponseModel.getUser().getPassportNumber()
                : Errors.DATA_NOT_FOUND.name() ); }
}
