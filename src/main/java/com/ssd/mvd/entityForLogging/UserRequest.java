package com.ssd.mvd.entityForLogging;

import com.ssd.mvd.controller.DataValidationInspector;
import com.ssd.mvd.entity.ApiResponseModel;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.constants.Errors;

public final class UserRequest extends DataValidationInspector {
    public void setCreatedAt( final long createdAt ) {
        this.createdAt = createdAt;
    }

    public void setPersonInfo( final PersonInfo personInfo ) {
        this.personInfo = personInfo;
    }

    public void setUserPassportNumber( final String userPassportNumber ) {
        this.userPassportNumber = userPassportNumber;
    }

    public void setIntegratedServiceName( final String integratedServiceName ) {
        this.integratedServiceName = integratedServiceName;
    }

    private long createdAt;
    private PersonInfo personInfo;

    private String userPassportNumber;
    private String integratedServiceName;
    private final String microserviceName = "api-findface";

    public UserRequest (
            final PsychologyCard psychologyCard,
            final ApiResponseModel apiResponseModel ) {
        this.setCreatedAt( super.newDate().getTime() );
        this.setPersonInfo( new PersonInfo( psychologyCard ) );

        this.setIntegratedServiceName( "OVIR" );
        this.setUserPassportNumber(
                super.checkObject( apiResponseModel.getUser() )
                ? apiResponseModel.getUser().getPassportNumber()
                : Errors.DATA_NOT_FOUND.name() );
    }
}
