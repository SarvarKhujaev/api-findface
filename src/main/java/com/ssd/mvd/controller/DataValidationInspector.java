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

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.Objects;
import java.util.List;

public class DataValidationInspector {
    protected static final DataValidationInspector INSTANCE = new DataValidationInspector();

    public static DataValidationInspector getInstance () { return INSTANCE; }

    public  <T> Mono< T > convert ( final T o ) { return Mono.just( o ); }

    public final Predicate< Object > checkObject = Objects::nonNull;

    protected final Predicate< String > checkParam = param -> param != null && !param.isEmpty();

    public final Function< Pinpp, String > joinString = pinpp -> String.join( " ", pinpp.getName(), pinpp.getSurname(), pinpp.getPatronym() );

    protected final Function< Integer, Integer > checkDifference = integer -> integer > 0 && integer < 100 ? integer : 10;

    protected final BiPredicate< HttpClientResponse, ByteBufMono > checkResponse =
            ( httpClientResponse, byteBufMono ) -> this.checkObject.test( byteBufMono )
            && httpClientResponse.status().code() == 200;

    protected final BiPredicate< Person, Pinpp > checkPerson = ( person, pinpp ) ->
            person.getPDateBirth().equals( pinpp.getBirthDate() )
            && person.getPPerson().contains( pinpp.getName() );

    public final BiPredicate< Integer, Object > checkData = ( integer, o ) -> switch ( integer ) {
            case 1 -> this.checkObject.test( ( (PsychologyCard) o ).getModelForCarList() )
                    && this.checkObject.test( ( (PsychologyCard) o )
                    .getModelForCarList()
                    .getModelForCarList() )
                    && ( (PsychologyCard) o )
                    .getModelForCarList()
                    .getModelForCarList()
                    .size() > 0;

            case 2 -> this.checkObject.test( ( (PsychologyCard) o).getModelForCadastr() )
                    && this.checkObject.test( ( (PsychologyCard) o )
                    .getModelForCadastr()
                    .getPermanentRegistration() )
                    && ( (PsychologyCard) o )
                    .getModelForCadastr()
                    .getPermanentRegistration().size() > 0;

            case 3 -> this.checkObject.test( ( o ) )
                    && this.checkObject.test( ( (ModelForPassport) o ).getData() )
                    && this.checkObject.test( ( (ModelForPassport) o ).getData().getPerson() )
                    && this.checkObject.test( ( (ModelForPassport) o ).getData().getPerson().getPinpp() )
                    && this.checkObject.test( ( (ModelForPassport) o ).getData().getPerson().getPCitizen() );

            case 4 -> this.checkObject.test( ( (CarTotalData) o ).getModelForCar() )
                    && this.checkObject.test( ( (CarTotalData) o ).getModelForCar().getPinpp() )
                    && !( (CarTotalData) o ).getModelForCar().getPinpp().isEmpty();

            case 5 -> this.checkObject.test( o ) && ( (List< ? >) o ).size() > 0;

            case 6 -> this.checkObject.test( ( o ) ) && this.checkData.test( 5, ( (ModelForCarList) o ).getModelForCarList() );

            case 7 -> this.checkObject.test( o ) && this.checkObject.test( ( (ModelForPassport) o ).getData().getDocument() );

            case 8 -> this.checkObject.test( o ) && this.checkObject.test( ( (ModelForAddress) o ).getPermanentRegistration() );

            default -> this.checkObject.test( ( (PsychologyCard) o ).getPinpp() )
                    && this.checkObject.test( ( (PsychologyCard) o ).getPinpp().getCadastre() )
                    && ( (PsychologyCard) o).getPinpp().getCadastre().length() > 1; };
}
