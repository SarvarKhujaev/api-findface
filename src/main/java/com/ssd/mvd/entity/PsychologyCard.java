package com.ssd.mvd.entity;

import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.entity.modelForCadastr.Data;
import com.ssd.mvd.inspectors.ErrorController;
import com.ssd.mvd.entity.foreigner.Foreigner;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Errors;

import reactor.util.function.*;
import java.util.List;

public final class PsychologyCard
        extends ErrorController
        implements EntityCommonMethods< PsychologyCard >, ServiceCommonMethods {
    private Pinpp pinpp;
    private String personImage; // the image of the person

    private List< PapilonData > papilonData;

    private List< Violation > violationList;
    private List< Foreigner > foreignerList;

    private ModelForCarList modelForCarList; // the list of all cars which belongs to this person
    private ModelForAddress modelForAddress;

    private ErrorResponse errorResponse;
    private ModelForPassport modelForPassport;
    private com.ssd.mvd.entity.modelForCadastr.Data modelForCadastr;

    public String getPersonImage() {
        return this.personImage;
    }

    public List< Violation > getViolationList() {
        return this.violationList;
    }

    public Pinpp getPinpp() {
        return this.pinpp;
    }

    public void setPinpp ( final Pinpp pinpp ) {
        this.pinpp = pinpp;
    }

    public void setPersonImage ( final String personImage ) {
        this.personImage = personImage;
    }

    public List< PapilonData > getPapilonData() {
        return this.papilonData;
    }

    public void setPapilonData ( final List< PapilonData > papilonData ) {
        this.papilonData = papilonData;
    }

    public void setViolationList ( final List< Violation > violationList ) {
        this.violationList = violationList;
    }

    public List< Foreigner > getForeignerList() {
        return this.foreignerList;
    }

    public void setForeignerList ( final List< Foreigner > foreignerList ) {
        this.foreignerList = foreignerList;
    }

    public ModelForCarList getModelForCarList() {
        return this.modelForCarList;
    }

    public void setModelForCarList ( final ModelForCarList modelForCarList ) {
        this.modelForCarList = modelForCarList;
    }

    public ModelForAddress getModelForAddress() {
        return this.modelForAddress;
    }

    public void setModelForAddress ( final ModelForAddress modelForAddress ) {
        this.modelForAddress = modelForAddress;
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    public ModelForPassport getModelForPassport() {
        return this.modelForPassport;
    }

    public void setModelForPassport ( @lombok.NonNull final ModelForPassport modelForPassport ) {
        this.modelForPassport = modelForPassport;
    }

    public Data getModelForCadastr() {
        return this.modelForCadastr;
    }

    public void setModelForCadastr ( @lombok.NonNull final Data modelForCadastr ) {
        this.modelForCadastr = modelForCadastr;
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public PsychologyCard setErrorResponse ( @lombok.NonNull final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public PsychologyCard save ( @lombok.NonNull final Results results ) {
        this.setViolationList( results.getViolationList() );
        this.setPapilonData( results.getResults() );
        return this;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public PsychologyCard save ( @lombok.NonNull final com.ssd.mvd.entity.modelForCadastr.Data data ) {
        this.setModelForCadastr( data );
        return this;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public PsychologyCard save ( @lombok.NonNull final Tuple2< ModelForAddress, ModelForPassport > tuple2 ) {
        this.setModelForPassport( tuple2.getT2() );
        this.setModelForAddress( tuple2.getT1() );
        return this;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public PsychologyCard save ( @lombok.NonNull final ModelForPassport data ) {
        this.setModelForPassport( data );
        return this;
    }

    public PsychologyCard () {}

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    public static PsychologyCard generate ( @lombok.NonNull final Results results ) {
        return new PsychologyCard( results );
    }

    @Override
    @lombok.NonNull
    public PsychologyCard generate() {
        return new PsychologyCard();
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public PsychologyCard generate (
            @lombok.NonNull final ErrorResponse errorResponse
    ) {
        return this.generate().setErrorResponse( errorResponse );
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _ -> this" )
    public PsychologyCard generate(
            @lombok.NonNull final String message,
            @lombok.NonNull final Errors errors
    ) {
        return this.generate().setErrorResponse(
                super.error.apply(
                        message,
                        errors
                )
        );
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _ -> _" )
    public static PsychologyCard generate (
            @lombok.NonNull final Results results,
            @lombok.NonNull final Tuple2< Pinpp, String > tuple
    ) {
        return new PsychologyCard( results, tuple );
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _ -> _" )
    public static PsychologyCard generate (
            @lombok.NonNull final ModelForPassport data,
            @lombok.NonNull final Tuple5<
                    Pinpp,
                    String,
                    ModelForCarList,
                    ModelForAddress,
                    List
                    > tuple
    ) {
        return new PsychologyCard( data, tuple );
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    public static PsychologyCard generate (
            @lombok.NonNull final Tuple4<
                    Pinpp,
                    String,
                    ModelForCarList,
                    List
                    > tuple
    ) {
        return new PsychologyCard( tuple );
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    public static PsychologyCard generate (
            @lombok.NonNull final Tuple2< Pinpp, String > tuple
    ) {
        return new PsychologyCard( tuple );
    }

    private PsychologyCard ( @lombok.NonNull final Results results ) {
        this.setPapilonData( results.getResults() );
        this.setViolationList( results.getViolationList() );
    }

    private PsychologyCard(
            @lombok.NonNull final Results results,
            @lombok.NonNull final Tuple2< Pinpp, String > tuple
    ) {
        this.setViolationList( results.getViolationList() );
        this.setPapilonData( results.getResults() );
        this.setPersonImage( tuple.getT2() );
        this.setPinpp( tuple.getT1() );
    }

    // for Passport request
    private PsychologyCard (
            @lombok.NonNull final ModelForPassport data,
            @lombok.NonNull final Tuple5<
                    Pinpp,
                    String,
                    ModelForCarList,
                    ModelForAddress,
                    List
                    > tuple
    ) {
        this.setModelForAddress( tuple.getT4() );
        this.setModelForCarList( tuple.getT3() );
        this.setViolationList( tuple.getT5() );
        this.setPersonImage( tuple.getT2() );
        this.setModelForPassport( data );
        this.setModelForPassport( data );
        this.setPinpp( tuple.getT1() );
    }

    // for PINFL request
    private PsychologyCard (
            @lombok.NonNull final Tuple4<
                    Pinpp,
                    String,
                    ModelForCarList,
                    List
                    > tuple
    ) {
        this.setModelForCarList( tuple.getT3() );
        this.setViolationList( tuple.getT4() );
        this.setPersonImage( tuple.getT2() );
        this.setPinpp( tuple.getT1() );
    }

    // for PINFL request
    private PsychologyCard ( @lombok.NonNull final Tuple2< Pinpp, String > tuple ) {
        this.setPersonImage( tuple.getT2() );
        this.setPinpp( tuple.getT1() );
    }

    @Override
    public void close() {
        this.getPapilonData().clear();
        this.getForeignerList().clear();
        this.getViolationList().clear();
        this.getModelForCarList().close();
        this.getModelForCadastr().close();
        this.getModelForAddress().close();
    }
}