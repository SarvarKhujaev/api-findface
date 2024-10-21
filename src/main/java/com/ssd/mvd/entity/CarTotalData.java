package com.ssd.mvd.entity;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.annotations.WeakReferenceAnnotation;

import com.ssd.mvd.inspectors.DataValidationInspector;
import com.ssd.mvd.inspectors.CustomServiceCleaner;
import com.ssd.mvd.inspectors.AnnotationInspector;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.entity.modelForGai.*;

import reactor.util.function.Tuple5;

public final class CarTotalData implements EntityCommonMethods< CarTotalData > {
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

    @WeakReferenceAnnotation( name = "tonirovka", isCollection = false )
    private Tonirovka tonirovka;
    @WeakReferenceAnnotation( name = "insurance", isCollection = false )
    private Insurance insurance;
    @WeakReferenceAnnotation( name = "modelForCar", isCollection = false )
    private ModelForCar modelForCar;
    @WeakReferenceAnnotation( name = "psychologyCard", isCollection = false )
    private PsychologyCard psychologyCard;
    @WeakReferenceAnnotation( name = "violationsList", isCollection = false )
    private ViolationsList violationsList;
    @WeakReferenceAnnotation( name = "doverennostList", isCollection = false )
    private DoverennostList doverennostList;
    @WeakReferenceAnnotation( name = "modelForCarList", isCollection = false )
    private ModelForCarList modelForCarList; // the list of all cars of each citizen

    @WeakReferenceAnnotation( name = "errorResponse", isCollection = false )
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

    @EntityConstructorAnnotation
    public <T> CarTotalData ( @lombok.NonNull final Class<T> instance ) {
        AnnotationInspector.checkCallerPermission( instance, CarTotalData.class );
        AnnotationInspector.checkAnnotationIsImmutable( CarTotalData.class );
    }

    private CarTotalData () {}

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
        CustomServiceCleaner.clearReference( this.violationsList );
        CustomServiceCleaner.clearReference( this.psychologyCard );
        CustomServiceCleaner.clearReference( this.doverennostList );
        CustomServiceCleaner.clearReference( this.modelForCarList );

        if (
                DataValidationInspector.objectIsNotNull(
                        this.getModelForCar()
                )
                && DataValidationInspector.objectIsNotNull(
                        this.getModelForCar().getDoverennostList()
                )
        ) {
            CustomServiceCleaner.clearReference( this.getModelForCar().getDoverennostList() );
        }
    }
}
