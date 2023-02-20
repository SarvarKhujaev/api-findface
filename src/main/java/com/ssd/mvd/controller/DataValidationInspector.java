package com.ssd.mvd.controller;

import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.entity.CarTotalData;

import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.ByteBufMono;
import lombok.Data;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.List;

@Data
public class DataValidationInspector {
    private static DataValidationInspector INSTANCE = new DataValidationInspector();

    public static DataValidationInspector getInstance() { return INSTANCE != null ? INSTANCE : ( INSTANCE = new DataValidationInspector() ); }

    private final Predicate< String > checkParam = param ->
            param != null
            && !param.isEmpty();

    private final Predicate< ModelForPassport > checkPassport = modelForPassport ->
            modelForPassport != null
            && modelForPassport.getData() != null
            && modelForPassport.getData().getPerson() != null
            && modelForPassport.getData().getPerson().getPinpp() != null
            && modelForPassport.getData().getPerson().getPCitizen() != null;

    private final Predicate< CarTotalData > checkCarTotalData = carTotalData ->
            carTotalData.getModelForCar() != null
            && carTotalData.getModelForCar().getPinpp() != null
            && !carTotalData.getModelForCar().getPinpp().isEmpty();

    private final Predicate< List< ? > > checkList = list ->
            list != null
            && list.size() > 0;

    private final BiFunction< HttpClientResponse, ByteBufMono, Boolean > checkResponse =
            ( httpClientResponse, byteBufMono ) -> byteBufMono != null && httpClientResponse.status().code() == 200;

    private final BiFunction< Integer, PsychologyCard, Boolean > checkData = ( integer, psychologyCard ) -> switch ( integer ) {
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
}
