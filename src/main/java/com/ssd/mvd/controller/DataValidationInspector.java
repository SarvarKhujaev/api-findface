package com.ssd.mvd.controller;

import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.modelForCadastr.Person;
import com.ssd.mvd.entity.ModelForCarList;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.entity.CarTotalData;
import com.ssd.mvd.entity.Pinpp;

import reactor.netty.http.client.HttpClientResponse;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;

import java.util.Optional;
import java.util.List;

public class DataValidationInspector extends CollectionsInspector {
    protected <T> Mono< T > convert ( final T o ) {
        return Optional.ofNullable( o ).isPresent() ? Mono.just( o ) : Mono.empty();
    }

    protected boolean checkObject ( final Object o ) {
        return o != null;
    }

    protected boolean checkParam ( final String param ) {
        return this.checkObject( param ) && !param.isEmpty();
    }

    protected int checkDifference ( final int value ) {
        return value > 0 && value < 100 ? value : 10;
    }

    protected boolean checkResponse (
            final HttpClientResponse httpClientResponse,
            final ByteBufMono byteBufMono
    ) {
        return this.checkObject( byteBufMono )
                && httpClientResponse.status().code() == 200;
    }

    protected boolean checkPerson (
            final Person person,
            final Pinpp pinpp
    ) {
        return person.getPDateBirth().equals( pinpp.getBirthDate() )
                && person.getPPerson().contains( pinpp.getName() );
    }

    public boolean check ( final List< ? > list ) {
        return this.checkObject( list ) && !list.isEmpty();
    }

    public boolean check ( final CarTotalData carTotalData ) {
        return this.checkObject( carTotalData.getModelForCar() )
                && this.checkObject( carTotalData.getModelForCar().getPinpp() )
                && !carTotalData.getModelForCar().getPinpp().isEmpty();
    }

    public boolean check ( final PsychologyCard psychologyCard ) {
        return this.checkObject( psychologyCard.getModelForCarList() )
                && this.checkObject( psychologyCard
                .getModelForCarList()
                .getModelForCarList() )
                && !psychologyCard
                .getModelForCarList()
                .getModelForCarList()
                .isEmpty();
    }

    public boolean check ( final ModelForCarList modelForCarList ) {
        return this.checkObject( modelForCarList ) && this.check( modelForCarList.getModelForCarList() );
    }

    public boolean check ( final ModelForAddress modelForAddress ) {
        return this.checkObject( modelForAddress ) && this.checkObject( modelForAddress.getPermanentRegistration() );
    }

    public boolean check ( final ModelForPassport modelForPassport ) {
        return this.checkObject( modelForPassport )
                && this.checkObject( modelForPassport.getData() )
                && this.checkObject( modelForPassport.getData().getPerson() )
                && this.checkObject( modelForPassport.getData().getPerson().getPinpp() )
                && this.checkObject( modelForPassport.getData().getPerson().getPCitizen() );
    }

    public boolean checkPinpp ( final PsychologyCard psychologyCard ) {
        return this.checkObject( psychologyCard.getPinpp() )
                && this.checkObject( psychologyCard.getPinpp().getCadastre() )
                && psychologyCard.getPinpp().getCadastre().length() > 1;
    }

    public boolean checkCadastor ( final PsychologyCard psychologyCard ) {
        return this.checkObject( psychologyCard.getModelForCadastr() )
                && this.checkObject(
                        psychologyCard
                            .getModelForCadastr()
                            .getPermanentRegistration() )
                && !psychologyCard
                .getModelForCadastr()
                .getPermanentRegistration().isEmpty();
    }

    public boolean checkPassport ( final ModelForPassport modelForPassport ) {
        return this.checkObject( modelForPassport )
                && this.checkObject( modelForPassport.getData().getDocument() );
    }
}
