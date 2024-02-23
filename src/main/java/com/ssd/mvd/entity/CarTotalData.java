package com.ssd.mvd.entity;

import reactor.util.function.Tuple5;
import com.ssd.mvd.entity.modelForGai.*;
import com.ssd.mvd.constants.ErrorResponse;

public final class CarTotalData {
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

    public void setErrorResponse (
            final ErrorResponse errorResponse
    ) {
        this.errorResponse = errorResponse;
    }

    private String gosNumber;
    private String cameraImage; // image which was made by camera

    private Tonirovka tonirovka;
    private Insurance insurance;
    private ModelForCar modelForCar;
    private PsychologyCard psychologyCard;
    private ViolationsList violationsList;
    private DoverennostList doverennostList;
    private ModelForCarList modelForCarList; // the list of all cars of each citizen

    private ErrorResponse errorResponse;

    public CarTotalData save ( final PsychologyCard psychologyCard ) {
        this.setPsychologyCard( psychologyCard );
        return this;
    }

    public static CarTotalData generate (
            final ModelForCar modelForCar
    ) {
        return new CarTotalData( modelForCar );
    }

    public static CarTotalData generate (
            final ErrorResponse errorResponse
    ) {
        return new CarTotalData( errorResponse );
    }

    public static CarTotalData generate (
            final Tuple5<
                    Tonirovka,
                    ModelForCar,
                    DoverennostList,
                    Insurance,
                    ViolationsList > objects
    ) {
        return new CarTotalData( objects );
    }

    private CarTotalData( final ModelForCar modelForCar ) {
        this.setModelForCar( modelForCar );
    }

    private CarTotalData ( final ErrorResponse errorResponse ) {
        this.setErrorResponse( errorResponse );
    }

    private CarTotalData ( final Tuple5<
            Tonirovka,
            ModelForCar,
            DoverennostList,
            Insurance,
            ViolationsList > objects ) {
        this.setDoverennostList( objects.getT3() );
        this.setViolationsList( objects.getT5() );
        this.setModelForCar( objects.getT2() );
        this.setTonirovka( objects.getT1() );
        this.setInsurance( objects.getT4() );
    }
}
