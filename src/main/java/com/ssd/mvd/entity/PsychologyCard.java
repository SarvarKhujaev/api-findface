package com.ssd.mvd.entity;

import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.entity.modelForCadastr.Data;
import com.ssd.mvd.inspectors.ErrorController;
import com.ssd.mvd.entity.foreigner.Foreigner;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Errors;

import com.ssd.mvd.interfaces.ServiceCommonMethods;
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

    public void setErrorResponse ( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
    }

    public ModelForPassport getModelForPassport() {
        return this.modelForPassport;
    }

    public void setModelForPassport ( final ModelForPassport modelForPassport ) {
        this.modelForPassport = modelForPassport;
    }

    public Data getModelForCadastr() {
        return this.modelForCadastr;
    }

    public void setModelForCadastr ( final Data modelForCadastr ) {
        this.modelForCadastr = modelForCadastr;
    }

    public PsychologyCard save ( final Results results ) {
        this.setViolationList( results.getViolationList() );
        this.setPapilonData( results.getResults() );
        return this;
    }

    public PsychologyCard save ( final com.ssd.mvd.entity.modelForCadastr.Data data ) {
        this.setModelForCadastr( data );
        return this;
    }

    public PsychologyCard save ( final Tuple2< ModelForAddress, ModelForPassport > tuple2 ) {
        this.setModelForPassport( tuple2.getT2() );
        this.setModelForAddress( tuple2.getT1() );
        return this;
    }

    // for Passport request
    public PsychologyCard save ( final ModelForPassport data ) {
        this.setModelForPassport( data );
        return this;
    }


    public static PsychologyCard generate ( final Results results ) {
        return new PsychologyCard( results );
    }

    public PsychologyCard () {}

    @Override
    public PsychologyCard generate (
            final ErrorResponse errorResponse
    ) {
        return new PsychologyCard( errorResponse );
    }

    @Override
    public PsychologyCard generate(
            final String message,
            final Errors errors
    ) {
        return new PsychologyCard().generate(
                super.error.apply(
                        message,
                        errors
                )
        );
    }

    public static PsychologyCard generate (
            final Results results,
            final Tuple2< Pinpp, String > tuple
    ) {
        return new PsychologyCard( results, tuple );
    }

    public static PsychologyCard generate (
            final ModelForPassport data,
            final Tuple5<
                    Pinpp,
                    String,
                    ModelForCarList,
                    ModelForAddress,
                    List
                    > tuple
    ) {
        return new PsychologyCard( data, tuple );
    }

    public static PsychologyCard generate (
            final Tuple4<
                    Pinpp,
                    String,
                    ModelForCarList,
                    List
                    > tuple
    ) {
        return new PsychologyCard( tuple );
    }

    public static PsychologyCard generate (
            final Tuple2< Pinpp, String > tuple
    ) {
        return new PsychologyCard( tuple );
    }

    private PsychologyCard ( final Results results ) {
        this.setPapilonData( results.getResults() );
        this.setViolationList( results.getViolationList() );
    }

    private PsychologyCard ( final ErrorResponse errorResponse ) {
        this.setErrorResponse( errorResponse );
    }

    private PsychologyCard(
            final Results results,
            final Tuple2< Pinpp, String > tuple
    ) {
        this.setViolationList( results.getViolationList() );
        this.setPapilonData( results.getResults() );
        this.setPersonImage( tuple.getT2() );
        this.setPinpp( tuple.getT1() );
    }

    // for Passport request
    private PsychologyCard (
            final ModelForPassport data,
            final Tuple5<
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
            final Tuple4<
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
    private PsychologyCard ( final Tuple2< Pinpp, String > tuple ) {
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