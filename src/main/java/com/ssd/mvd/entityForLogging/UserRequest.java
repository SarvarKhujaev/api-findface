package com.ssd.mvd.entityForLogging;

import com.ssd.mvd.controller.DataValidationInspector;
import com.ssd.mvd.entity.ApiResponseModel;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.constants.Errors;
import java.util.Date;

@lombok.Data
public final class UserRequest {
    private Long createdAt;
    private PersonInfo personInfo;

    private String userPassportNumber;
    private String integratedServiceName;
    private final String microserviceName = "api-findface";

    public UserRequest (final PsychologyCard psychologyCard,
                        final ApiResponseModel apiResponseModel,
                        final DataValidationInspector dataValidationInspector ) {
        this.setCreatedAt( new Date().getTime() );
        this.setPersonInfo( new PersonInfo( psychologyCard, dataValidationInspector ) );

        this.setIntegratedServiceName( "OVIR" );
        this.setUserPassportNumber(
                dataValidationInspector.checkObject.test( apiResponseModel.getUser() )
                ? apiResponseModel.getUser().getPassportNumber()
                : Errors.DATA_NOT_FOUND.name() ); }
}
