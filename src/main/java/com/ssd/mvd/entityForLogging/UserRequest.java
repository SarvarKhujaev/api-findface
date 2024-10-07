package com.ssd.mvd.entityForLogging;

import com.ssd.mvd.inspectors.DataValidationInspector;
import com.ssd.mvd.entity.response.ApiResponseModel;
import com.ssd.mvd.interfaces.KafkaCommonMethods;
import com.ssd.mvd.inspectors.EntitiesInstances;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.inspectors.Config;
import com.ssd.mvd.constants.Errors;

import com.google.gson.annotations.Expose;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public final class UserRequest extends DataValidationInspector implements KafkaCommonMethods {
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

    public UserRequest () {}

    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_, _ -> !null" )
    public UserRequest update (
            @lombok.NonNull final PsychologyCard psychologyCard,
            @lombok.NonNull final ApiResponseModel apiResponseModel
    ) {
        this.setCreatedAt( super.newDate().get().getTime() );
        EntitiesInstances.PERSON_INFO_ATOMIC_REFERENCE.getAndUpdate( personInfo1 -> {
            this.setPersonInfo( personInfo1.update( psychologyCard ) );
            return personInfo1;
        } );

        this.setUserPassportNumber(
                super.objectIsNotNull( apiResponseModel.getUser() )
                    ? apiResponseModel.getUser().getPassportNumber()
                    : Errors.DATA_NOT_FOUND.name()
        );

        return this;
    }

    @Override
    @lombok.NonNull
    public String getTopicName() {
        return Config.getADMIN_PANEL();
    }

    @Override
    @lombok.NonNull
    public String getSuccessMessage() {
        return String.join(
                SPACE,
                "New user exposed your service: ",
                this.getTopicName(),
                this.userPassportNumber,
                " at: ",
                super.newDate().get().toString(),
                integratedServiceName,
                microserviceName,
                String.valueOf( createdAt )
        );
    }

    @Override
    @lombok.NonNull
    public String getCompletedMessage() {
        return String.join(
                SPACE,
                "New user exposed your service: ",
                this.getTopicName(),
                this.userPassportNumber,
                " at: ",
                super.newDate().get().toString(),
                integratedServiceName,
                microserviceName,
                String.valueOf( createdAt ),
                this.personInfo.toString()
        );
    }
}
