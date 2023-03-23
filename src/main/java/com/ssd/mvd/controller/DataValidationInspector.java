package com.ssd.mvd.controller;

import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.modelForCadastr.Person;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.entity.CarTotalData;
import com.ssd.mvd.entity.Pinpp;

import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.ByteBufMono;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.List;

@lombok.Data
public class DataValidationInspector {
    public final Predicate< String > checkParam = param -> param != null && !param.isEmpty();

    public final Predicate< ModelForPassport > checkPassport = modelForPassport ->
            modelForPassport != null
            && modelForPassport.getData() != null
            && modelForPassport.getData().getPerson() != null
            && modelForPassport.getData().getPerson().getPinpp() != null
            && modelForPassport.getData().getPerson().getPCitizen() != null;

    public final Predicate< CarTotalData > checkCarTotalData = carTotalData ->
            carTotalData.getModelForCar() != null
            && carTotalData.getModelForCar().getPinpp() != null
            && !carTotalData.getModelForCar().getPinpp().isEmpty();

    public final Predicate< List< ? > > checkList = list -> list != null && list.size() > 0;

    public final BiFunction< HttpClientResponse, ByteBufMono, Boolean > checkResponse =
            ( httpClientResponse, byteBufMono ) -> byteBufMono != null && httpClientResponse.status().code() == 200;

    public final BiFunction< Integer, PsychologyCard, Boolean > checkData = ( integer, psychologyCard ) -> switch ( integer ) {
        case 1 -> psychologyCard.getModelForCarList() != null
                && psychologyCard
                .getModelForCarList()
                .getModelForCarList() != null
                && psychologyCard
                .getModelForCarList()
                .getModelForCarList()
                .size() > 0;

        case 2 -> psychologyCard.getModelForCadastr() != null
                && psychologyCard
                .getModelForCadastr()
                .getPermanentRegistration() != null
                && psychologyCard
                .getModelForCadastr()
                .getPermanentRegistration().size() > 0;

        default -> psychologyCard.getPinpp() != null
                && psychologyCard.getPinpp().getCadastre() != null
                && psychologyCard.getPinpp().getCadastre().length() > 1; };

    public final BiFunction< Person, Pinpp, Boolean > checkPerson = ( person, pinpp ) ->
            person.getPDateBirth().equals( pinpp.getBirthDate() )
            && person.getPPerson().contains( pinpp.getName() );
}
