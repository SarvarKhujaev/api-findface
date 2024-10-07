package com.ssd.mvd.inspectors;

import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.modelForCadastr.Person;
import com.ssd.mvd.FindFaceServiceApplication;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.entity.CarTotalData;
import com.ssd.mvd.entity.Pinpp;

import reactor.netty.http.client.HttpClientResponse;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;

import java.util.Objects;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public class DataValidationInspector extends CollectionsInspector {
    protected DataValidationInspector () {}

    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    protected final synchronized <T> Mono< T > convert ( final T o ) {
        return Mono.justOrEmpty( o );
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized boolean objectIsNotNull ( final Object o ) {
        return Objects.nonNull( o );
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized boolean checkParam ( final String param ) {
        return this.objectIsNotNull( param ) && !param.isEmpty();
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized int checkDifference ( final int value ) {
        return value > 0 && value < 100 ? value : 10;
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_, _ -> _" )
    protected final synchronized boolean checkResponse (
            final HttpClientResponse httpClientResponse,
            final ByteBufMono byteBufMono
    ) {
        return this.objectIsNotNull( byteBufMono )
                && httpClientResponse.status().code() == 200;
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_, _ -> _" )
    protected final synchronized boolean checkPerson (
            @lombok.NonNull final Person person,
            @lombok.NonNull final Pinpp pinpp
    ) {
        return person.getPDateBirth().equals( pinpp.getBirthDate() )
                && person.getPPerson().contains( pinpp.getName() );
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized boolean check ( final CarTotalData carTotalData ) {
        return this.objectIsNotNull( carTotalData.getModelForCar() )
                && this.objectIsNotNull( carTotalData.getModelForCar().getPinpp() )
                && !carTotalData.getModelForCar().getPinpp().isEmpty();
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized boolean check ( final PsychologyCard psychologyCard ) {
        return this.objectIsNotNull( psychologyCard.getModelForCarList() )
                && this.objectIsNotNull( psychologyCard
                .getModelForCarList()
                .getModelForCarList() )
                && !psychologyCard
                .getModelForCarList()
                .getModelForCarList()
                .isEmpty();
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized boolean check ( final ModelForAddress modelForAddress ) {
        return this.objectIsNotNull( modelForAddress ) && this.objectIsNotNull( modelForAddress.getPermanentRegistration() );
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized boolean check ( final ModelForPassport modelForPassport ) {
        return this.objectIsNotNull( modelForPassport )
                && this.objectIsNotNull( modelForPassport.getData() )
                && this.objectIsNotNull( modelForPassport.getData().getPerson() )
                && this.objectIsNotNull( modelForPassport.getData().getPerson().getPinpp() )
                && this.objectIsNotNull( modelForPassport.getData().getPerson().getPCitizen() );
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized boolean checkPinpp ( final PsychologyCard psychologyCard ) {
        return this.objectIsNotNull( psychologyCard.getPinpp() )
                && this.objectIsNotNull( psychologyCard.getPinpp().getCadastre() )
                && psychologyCard.getPinpp().getCadastre().length() > 1;
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized boolean checkCadastor ( final PsychologyCard psychologyCard ) {
        return this.objectIsNotNull( psychologyCard.getModelForCadastr() )
                && this.objectIsNotNull(
                        psychologyCard
                            .getModelForCadastr()
                            .getPermanentRegistration() )
                && !psychologyCard
                .getModelForCadastr()
                .getPermanentRegistration().isEmpty();
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized boolean checkPassport ( final ModelForPassport modelForPassport ) {
        return this.objectIsNotNull( modelForPassport )
                && this.objectIsNotNull( modelForPassport.getData().getDocument() );
    }

    @SuppressWarnings(
            value = """
                    получает в параметрах название параметра из файла application.yaml
                    проверят что context внутри main класса GpsTabletsServiceApplication  инициализирован
                    и среди параметров сервиса сузествует переданный параметр
                    """
    )
    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_, _ -> _" )
    public static synchronized String checkContextOrReturnDefaultValue (
            @lombok.NonNull final String paramName,
            @lombok.NonNull final String defaultValue
    ) {
        return Objects.requireNonNullElse(
                FindFaceServiceApplication
                        .context
                        .getEnvironment()
                        .getProperty( paramName ),
                defaultValue
        );
    }

    @lombok.Synchronized
    protected static synchronized int checkContextOrReturnDefaultValue () {
        return Objects.nonNull( FindFaceServiceApplication.context )
                && Objects.nonNull(
                        FindFaceServiceApplication
                                .context
                                .getEnvironment()
                                .getProperty( "variables.KAFKA_VARIABLES.KAFKA_SENDER_MAX_IN_FLIGHT" )
                )
                ? Integer.parseInt(
                        Objects.requireNonNull(
                                FindFaceServiceApplication
                                        .context
                                        .getEnvironment()
                                        .getProperty( "variables.KAFKA_VARIABLES.KAFKA_SENDER_MAX_IN_FLIGHT" )
                        )
                )
                : 1024;
    }
}
