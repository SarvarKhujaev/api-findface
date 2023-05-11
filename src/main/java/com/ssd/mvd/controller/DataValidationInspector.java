package com.ssd.mvd.controller;

import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.modelForCadastr.Person;
import com.ssd.mvd.entity.ModelForCarList;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.entity.CarTotalData;
import com.ssd.mvd.entity.Pinpp;

import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.ByteBufMono;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.Objects;
import java.util.List;

@lombok.Data
public class DataValidationInspector {
    private final Predicate< Object > checkObject = Objects::nonNull;

    private final Predicate< String > checkParam = param -> param != null && !param.isEmpty();

    private final Function< Integer, Integer > checkDifference = integer -> integer > 0 && integer < 100 ? integer : 10;

    private final BiPredicate< HttpClientResponse, ByteBufMono > checkResponse =
            ( httpClientResponse, byteBufMono ) -> this.getCheckObject().test( byteBufMono )
            && httpClientResponse.status().code() == 200;

    private final BiPredicate< Person, Pinpp > checkPerson = ( person, pinpp ) ->
            person.getPDateBirth().equals( pinpp.getBirthDate() )
            && person.getPPerson().contains( pinpp.getName() );

    private final BiPredicate< Integer, Object > checkData = ( integer, o ) -> switch ( integer ) {
            case 1 -> this.getCheckObject().test( ( (PsychologyCard) o ).getModelForCarList() )
                    && this.getCheckObject().test( ( (PsychologyCard) o )
                    .getModelForCarList()
                    .getModelForCarList() )
                    && ( (PsychologyCard) o )
                    .getModelForCarList()
                    .getModelForCarList()
                    .size() > 0;

            case 2 -> this.getCheckObject().test( ( (PsychologyCard) o).getModelForCadastr() )
                    && this.getCheckObject().test( ( (PsychologyCard) o )
                    .getModelForCadastr()
                    .getPermanentRegistration() )
                    && ( (PsychologyCard) o )
                    .getModelForCadastr()
                    .getPermanentRegistration().size() > 0;

            case 3 -> this.getCheckObject().test( ( o ) )
                    && this.getCheckObject().test( ( (ModelForPassport) o ).getData() )
                    && this.getCheckObject().test( ( (ModelForPassport) o ).getData().getPerson() )
                    && this.getCheckObject().test( ( (ModelForPassport) o ).getData().getPerson().getPinpp() )
                    && this.getCheckObject().test( ( (ModelForPassport) o ).getData().getPerson().getPCitizen() );

            case 4 -> this.getCheckObject().test( ( (CarTotalData) o ).getModelForCar() )
                    && this.getCheckObject().test( ( (CarTotalData) o ).getModelForCar().getPinpp() )
                    && !( (CarTotalData) o ).getModelForCar().getPinpp().isEmpty();

            case 5 -> this.getCheckObject().test( o ) && ( (List< ? >) o ).size() > 0;

            case 6 -> this.getCheckObject().test( ( o ) ) && this.getCheckData().test( 5, ( (ModelForCarList) o ).getModelForCarList() );

            default -> this.getCheckObject().test( ( (PsychologyCard) o ).getPinpp() )
                    && this.getCheckObject().test( ( (PsychologyCard) o ).getPinpp().getCadastre() )
                    && ( (PsychologyCard) o).getPinpp().getCadastre().length() > 1; };
}
