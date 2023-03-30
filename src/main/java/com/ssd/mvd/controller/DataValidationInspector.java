package com.ssd.mvd.controller;

import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.modelForCadastr.Person;
import com.ssd.mvd.entity.ModelForCarList;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.entity.CarTotalData;
import com.ssd.mvd.entity.Pinpp;

import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.ByteBufMono;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.List;

@lombok.Data
public class DataValidationInspector {
    public final Predicate< Object > checkObject = Objects::nonNull;

    public final Predicate< String > checkParam = param -> param != null && !param.isEmpty();

    public final Predicate< ModelForPassport > checkPassport = modelForPassport ->
            this.checkObject.test( modelForPassport )
            && modelForPassport.getData() != null
            && modelForPassport.getData().getPerson() != null
            && modelForPassport.getData().getPerson().getPinpp() != null
            && modelForPassport.getData().getPerson().getPCitizen() != null;

    public final Predicate< CarTotalData > checkCarTotalData = carTotalData ->
            this.checkObject.test( carTotalData.getModelForCar() )
            && this.checkObject.test( carTotalData.getModelForCar().getPinpp() )
            && !carTotalData.getModelForCar().getPinpp().isEmpty();

    public final Predicate< List< ? > > checkList = list -> this.checkObject.test( list ) && list.size() > 0;

    public final BiFunction< HttpClientResponse, ByteBufMono, Boolean > checkResponse =
            ( httpClientResponse, byteBufMono ) -> this.checkObject.test( byteBufMono ) && httpClientResponse.status().code() == 200;

    public final BiFunction< Integer, PsychologyCard, Boolean > checkData = ( integer, psychologyCard ) -> switch ( integer ) {
        case 1 -> this.checkObject.test( psychologyCard.getModelForCarList() )
                && this.checkObject.test( psychologyCard
                .getModelForCarList()
                .getModelForCarList() )
                && psychologyCard
                .getModelForCarList()
                .getModelForCarList()
                .size() > 0;

        case 2 -> this.checkObject.test( psychologyCard.getModelForCadastr() )
                && this.checkObject.test( psychologyCard
                .getModelForCadastr()
                .getPermanentRegistration() )
                && psychologyCard
                .getModelForCadastr()
                .getPermanentRegistration().size() > 0;

        default -> this.checkObject.test( psychologyCard.getPinpp() )
                && this.checkObject.test( psychologyCard.getPinpp().getCadastre() )
                && psychologyCard.getPinpp().getCadastre().length() > 1; };

    public final BiFunction< Person, Pinpp, Boolean > checkPerson = ( person, pinpp ) ->
            person.getPDateBirth().equals( pinpp.getBirthDate() )
            && person.getPPerson().contains( pinpp.getName() );

    public final Predicate< ModelForCarList > checkCarList = modelForCarList ->
            this.checkObject.test( modelForCarList )
            && this.checkObject.test( modelForCarList.getModelForCarList() )
            && modelForCarList.getModelForCarList().size() > 0;
}
