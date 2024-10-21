package com.ssd.mvd.inspectors;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
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
public class DataValidationInspector extends AnnotationInspector {
    protected DataValidationInspector () {
        super( DataValidationInspector.class );
    }

    @EntityConstructorAnnotation( permission = CustomSerializer.class )
    protected <T extends UuidInspector> DataValidationInspector( @lombok.NonNull final Class<T> instance ) {
        super( DataValidationInspector.class );

        AnnotationInspector.checkCallerPermission( instance, DataValidationInspector.class );
        AnnotationInspector.checkAnnotationIsImmutable( DataValidationInspector.class );
    }

    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    protected final synchronized <T> Mono< T > convert ( final T o ) {
        return Mono.justOrEmpty( o );
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    public static synchronized boolean objectIsNotNull (
            final Object ... o
    ) {
        return o.length == 0
                ? Objects.nonNull( o[0] )
                : convertArrayToList( o )
                .stream()
                .filter( Objects::nonNull )
                .count() == o.length;
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized boolean checkParam ( final String param ) {
        return objectIsNotNull( param ) && !param.isEmpty();
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
        return objectIsNotNull( byteBufMono ) && httpClientResponse.status().code() == 200;
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
        return objectIsNotNull( carTotalData )
                && objectIsNotNull( carTotalData.getModelForCar() )
                && objectIsNotNull( carTotalData.getModelForCar().getPinpp() )
                && !carTotalData.getModelForCar().getPinpp().isEmpty();
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized boolean check ( final PsychologyCard psychologyCard ) {
        return objectIsNotNull( psychologyCard )
                && objectIsNotNull( psychologyCard.getModelForCarList() )
                && objectIsNotNull( psychologyCard
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
        return objectIsNotNull( modelForAddress ) && objectIsNotNull( modelForAddress.getPermanentRegistration() );
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized boolean check ( final ModelForPassport modelForPassport ) {
        return objectIsNotNull( modelForPassport )
                && objectIsNotNull( modelForPassport.getData() )
                && objectIsNotNull( modelForPassport.getData().getPerson() )
                && objectIsNotNull( modelForPassport.getData().getPerson().getPinpp() )
                && objectIsNotNull( modelForPassport.getData().getPerson().getPCitizen() );
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized boolean checkPinpp ( final PsychologyCard psychologyCard ) {
        return objectIsNotNull( psychologyCard.getPinpp() )
                && objectIsNotNull( psychologyCard.getPinpp().getCadastre() )
                && psychologyCard.getPinpp().getCadastre().length() > 1;
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized boolean checkCadastor ( final PsychologyCard psychologyCard ) {
        return objectIsNotNull( psychologyCard.getModelForCadastr() )
                && objectIsNotNull(
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
        return objectIsNotNull( modelForPassport )
                && objectIsNotNull( modelForPassport.getData().getDocument() );
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
