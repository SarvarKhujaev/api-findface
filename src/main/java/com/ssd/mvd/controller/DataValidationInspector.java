package com.ssd.mvd.controller;

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

public class DataValidationInspector extends CollectionsInspector {
    protected final synchronized <T> Mono< T > convert ( final T o ) {
        return this.objectIsNotNull( o ) ? Mono.just( o ) : Mono.empty();
    }

    protected final synchronized boolean objectIsNotNull ( final Object o ) {
        return o != null;
    }

    protected final synchronized boolean checkParam ( final String param ) {
        return this.objectIsNotNull( param ) && !param.isEmpty();
    }

    protected final synchronized int checkDifference ( final int value ) {
        return value > 0 && value < 100 ? value : 10;
    }

    protected final synchronized boolean checkResponse (
            final HttpClientResponse httpClientResponse,
            final ByteBufMono byteBufMono
    ) {
        return this.objectIsNotNull( byteBufMono )
                && httpClientResponse.status().code() == 200;
    }

    protected final synchronized boolean checkPerson (
            final Person person,
            final Pinpp pinpp
    ) {
        return person.getPDateBirth().equals( pinpp.getBirthDate() )
                && person.getPPerson().contains( pinpp.getName() );
    }

    protected final synchronized boolean check ( final CarTotalData carTotalData ) {
        return this.objectIsNotNull( carTotalData.getModelForCar() )
                && this.objectIsNotNull( carTotalData.getModelForCar().getPinpp() )
                && !carTotalData.getModelForCar().getPinpp().isEmpty();
    }

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

    protected final synchronized boolean check ( final ModelForAddress modelForAddress ) {
        return this.objectIsNotNull( modelForAddress ) && this.objectIsNotNull( modelForAddress.getPermanentRegistration() );
    }

    protected final synchronized boolean check ( final ModelForPassport modelForPassport ) {
        return this.objectIsNotNull( modelForPassport )
                && this.objectIsNotNull( modelForPassport.getData() )
                && this.objectIsNotNull( modelForPassport.getData().getPerson() )
                && this.objectIsNotNull( modelForPassport.getData().getPerson().getPinpp() )
                && this.objectIsNotNull( modelForPassport.getData().getPerson().getPCitizen() );
    }

    protected final synchronized boolean checkPinpp ( final PsychologyCard psychologyCard ) {
        return this.objectIsNotNull( psychologyCard.getPinpp() )
                && this.objectIsNotNull( psychologyCard.getPinpp().getCadastre() )
                && psychologyCard.getPinpp().getCadastre().length() > 1;
    }

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

    protected final synchronized boolean checkPassport ( final ModelForPassport modelForPassport ) {
        return this.objectIsNotNull( modelForPassport )
                && this.objectIsNotNull( modelForPassport.getData().getDocument() );
    }

    /*
    получает в параметрах название параметра из файла application.yaml
    проверят что context внутри main класса GpsTabletsServiceApplication  инициализирован
    и среди параметров сервиса сузествует переданный параметр
    */
    protected final synchronized <T> T checkContextOrReturnDefaultValue (
            final String paramName,
            final T defaultValue
    ) {
        return this.objectIsNotNull( FindFaceServiceApplication.context )
                && this.objectIsNotNull(
                        FindFaceServiceApplication
                                .context
                                .getEnvironment()
                                .getProperty( paramName )
                )
                ? (T) FindFaceServiceApplication
                        .context
                        .getEnvironment()
                        .getProperty( paramName )
                : defaultValue;
    }
}
