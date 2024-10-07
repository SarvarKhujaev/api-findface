package com.ssd.mvd.entity;

import reactor.util.function.Tuple5;

import com.ssd.mvd.entity.modelForGai.*;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.interfaces.ServiceCommonMethods;

public final class CarTotalData
        implements EntityCommonMethods< CarTotalData >, ServiceCommonMethods {
    public void setTonirovka ( final Tonirovka tonirovka ) {
        this.tonirovka = tonirovka;
    }

    public void setInsurance (
            final Insurance insurance
    ) {
        this.insurance = insurance;
    }

    public ModelForCar getModelForCar() {
        return this.modelForCar;
    }

    public void setModelForCar (
            final ModelForCar modelForCar
    ) {
        this.modelForCar = modelForCar;
    }

    public void setPsychologyCard (
            final PsychologyCard psychologyCard
    ) {
        this.psychologyCard = psychologyCard;
    }

    public void setViolationsList (
            final ViolationsList violationsList
    ) {
        this.violationsList = violationsList;
    }

    public void setDoverennostList (
            final DoverennostList doverennostList
    ) {
        this.doverennostList = doverennostList;
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public CarTotalData setErrorResponse (
            @lombok.NonNull final ErrorResponse errorResponse
    ) {
        this.errorResponse = errorResponse;
        return this;
    }

    private String gosNumber;
    private String cameraImage;

    private Tonirovka tonirovka;
    private Insurance insurance;
    private ModelForCar modelForCar;
    private PsychologyCard psychologyCard;
    private ViolationsList violationsList;
    private DoverennostList doverennostList;
    private ModelForCarList modelForCarList; // the list of all cars of each citizen

    private ErrorResponse errorResponse;

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    public CarTotalData save ( @lombok.NonNull final PsychologyCard psychologyCard ) {
        this.setPsychologyCard( psychologyCard );
        return this;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    public static CarTotalData generate (
            @lombok.NonNull final ModelForCar modelForCar
    ) {
        return new CarTotalData( modelForCar );
    }

    public CarTotalData () {}

    private CarTotalData( final ModelForCar modelForCar ) {
        this.setModelForCar( modelForCar );
    }

    @Override
    @lombok.NonNull
    public CarTotalData generate() {
        return new CarTotalData();
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    public static CarTotalData generate (
            @lombok.NonNull final Tuple5<
                    Tonirovka,
                    ModelForCar,
                    DoverennostList,
                    Insurance,
                    ViolationsList > objects
    ) {
        return new CarTotalData( objects );
    }

    private CarTotalData (
            @lombok.NonNull final Tuple5<
                Tonirovka,
                ModelForCar,
                DoverennostList,
                Insurance,
                ViolationsList > objects
    ) {
        this.setDoverennostList( objects.getT3() );
        this.setViolationsList( objects.getT5() );
        this.setModelForCar( objects.getT2() );
        this.setTonirovka( objects.getT1() );
        this.setInsurance( objects.getT4() );
    }

    @Override
    public void close() {
        this.violationsList.close();
        this.psychologyCard.close();
        this.doverennostList.close();
        this.modelForCarList.close();
        this.getModelForCar().getDoverennostList().close();
    }
}
