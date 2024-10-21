package com.ssd.mvd.entityForLogging;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.annotations.WeakReferenceAnnotation;
import com.ssd.mvd.annotations.AvroMethodAnnotation;
import com.ssd.mvd.annotations.AvroFieldAnnotation;

import com.ssd.mvd.interfaces.KafkaCommonMethods;
import com.ssd.mvd.constants.Errors;

import com.ssd.mvd.inspectors.DataValidationInspector;
import com.ssd.mvd.inspectors.AnnotationInspector;
import com.ssd.mvd.inspectors.EntitiesInstances;
import com.ssd.mvd.inspectors.Config;

import com.ssd.mvd.entity.response.ApiResponseModel;
import com.ssd.mvd.entity.PsychologyCard;
import org.apache.avro.Schema;

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

    @AvroMethodAnnotation( name = "createdAt" )
    public long getCreatedAt() {
        return this.createdAt;
    }

    @AvroMethodAnnotation( name = "microserviceName" )
    public String getMicroserviceName() {
        return this.microserviceName;
    }

    @AvroMethodAnnotation( name = "userPassportNumber" )
    public String getUserPassportNumber() {
        return this.userPassportNumber;
    }

    @AvroMethodAnnotation( name = "integratedServiceName" )
    public String getIntegratedServiceName() {
        return this.integratedServiceName;
    }

    @AvroFieldAnnotation( name = "createdAt", schemaType = Schema.Type.LONG )
    private long createdAt;

    @WeakReferenceAnnotation( name = "personInfo", isCollection = false )
    private PersonInfo personInfo;

    @AvroFieldAnnotation( name = "userPassportNumber" )
    private String userPassportNumber;

    @AvroFieldAnnotation( name = "microserviceName" )
    private final String microserviceName = "api-findface";
    @AvroFieldAnnotation( name = "integratedServiceName" )
    private final String integratedServiceName = "OVIR";

    @EntityConstructorAnnotation
    public <T> UserRequest ( @lombok.NonNull final Class<T> instance ) {
        AnnotationInspector.checkCallerPermission( instance, UserRequest.class );
        AnnotationInspector.checkAnnotationIsImmutable( UserRequest.class );
    }

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
                objectIsNotNull( apiResponseModel.getUser() )
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
                String.valueOf( this.createdAt )
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
                String.valueOf( this.createdAt ),
                this.personInfo.toString()
        );
    }
}
