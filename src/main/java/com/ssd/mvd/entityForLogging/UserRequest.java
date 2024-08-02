package com.ssd.mvd.entityForLogging;

import com.google.gson.annotations.Expose;

import com.ssd.mvd.interfaces.KafkaCommonMethods;
import com.ssd.mvd.entity.ApiResponseModel;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.inspectors.Config;
import com.ssd.mvd.constants.Errors;

public final class UserRequest extends Config implements KafkaCommonMethods {
    private void setCreatedAt( final long createdAt ) {
        this.createdAt = createdAt;
    }

    private void setPersonInfo( final PersonInfo personInfo ) {
        this.personInfo = personInfo;
    }

    private void setUserPassportNumber( final String userPassportNumber ) {
        this.userPassportNumber = userPassportNumber;
    }

    @Expose
    private long createdAt;

    @Expose
    private PersonInfo personInfo;

    @Expose
    private String userPassportNumber;

    @Expose
    private final static String integratedServiceName = "OVIR";

    @Expose
    private final static String microserviceName = "api-findface";

    public UserRequest (
            final PsychologyCard psychologyCard,
            final ApiResponseModel apiResponseModel
    ) {
        this.setCreatedAt( super.newDate().getTime() );
        this.setPersonInfo( new PersonInfo( psychologyCard ) );

        this.setUserPassportNumber(
                super.objectIsNotNull( apiResponseModel.getUser() )
                    ? apiResponseModel.getUser().getPassportNumber()
                    : Errors.DATA_NOT_FOUND.name()
        );
    }

    @Override
    public String getTopicName() {
        return super.getADMIN_PANEL();
    }

    @Override
    public String getSuccessMessage() {
        return String.join(
                " ",
                "New user exposed your service: ",
                super.getADMIN_PANEL(),
                this.userPassportNumber,
                " at: ",
                super.newDate().toString(),
                integratedServiceName,
                microserviceName,
                String.valueOf( createdAt )
        );
    }

    @Override
    public String getCompletedMessage() {
        return String.join(
                " ",
                "New user exposed your service: ",
                super.getADMIN_PANEL(),
                this.userPassportNumber,
                " at: ",
                super.newDate().toString(),
                integratedServiceName,
                microserviceName,
                String.valueOf( createdAt ),
                this.personInfo.toString()
        );
    }
}
