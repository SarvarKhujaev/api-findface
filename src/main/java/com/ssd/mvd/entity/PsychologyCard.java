package com.ssd.mvd.entity;

import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.foreigner.Foreigner;
import com.ssd.mvd.constants.ErrorResponse;

import reactor.util.function.*;
import java.util.List;

@lombok.Data
@lombok.NoArgsConstructor
public class PsychologyCard {
    private Pinpp pinpp;
    private String personImage; // the image of the person

    private List< PapilonData > papilonData;
    private List< Violation > violationList;
    private List< Foreigner > foreignerList;

    private ModelForCarList modelForCarList; // the list of all cars which belongs to this person
    private ModelForAddress modelForAddress;

    private ModelForPassport modelForPassport;
    private com.ssd.mvd.entity.modelForCadastr.Data modelForCadastr;

    private ErrorResponse errorResponse;

    public PsychologyCard save ( final com.ssd.mvd.entity.modelForCadastr.Data data ) {
        this.setModelForCadastr( data );
        return this; }

    public PsychologyCard save ( final Tuple2<
            ModelForAddress,
            ModelForPassport > tuple2 ) {
        this.setModelForPassport( tuple2.getT2() );
        this.setModelForAddress( tuple2.getT1() );
        return this; }

    public PsychologyCard ( final Results results ) {
        this.setPapilonData( results.getResults() );
        this.setViolationList( results.getViolationList() ); }

    public PsychologyCard ( final ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }

    public PsychologyCard( final Results results, final Tuple2< Pinpp, String > tuple ) {
        this.setViolationList( results.getViolationList() );
        this.setPapilonData( results.getResults() );
        this.setPersonImage( tuple.getT2() );
        this.setPinpp( tuple.getT1() ); }

    public PsychologyCard ( final Results results,
                            final Tuple3<
                                    Pinpp,
                                    String,
                                    ModelForCarList > tuple ) {
        this.setViolationList( results.getViolationList() );
        this.setPapilonData( results.getResults() );
        this.setModelForCarList( tuple.getT3() );
        this.setPersonImage( tuple.getT2() );
        this.setPinpp( tuple.getT1() ); }

    // for Passport request
    public PsychologyCard( final ModelForPassport data,
                           final Tuple2< Pinpp, String > tuple ) {
        this.setPersonImage( tuple.getT2() );
        this.setModelForPassport( data );
        this.setPinpp( tuple.getT1() ); }

    // for Passport request
    public PsychologyCard ( final ModelForPassport data,
                            final Tuple5<
                                    Pinpp,
                                    String,
                                    ModelForCarList,
                                    ModelForAddress,
                                    List > tuple ) {
        this.setModelForAddress( tuple.getT4() );
        this.setModelForCarList( tuple.getT3() );
        this.setViolationList( tuple.getT5() );
        this.setPersonImage( tuple.getT2() );
        this.setModelForPassport( data );
        this.setModelForPassport( data );
        this.setPinpp( tuple.getT1() ); }

    // for PINFL request
    public PsychologyCard ( final Tuple4<
                Pinpp,
                String,
                ModelForCarList,
                List > tuple ) {
        this.setModelForCarList( tuple.getT3() );
        this.setViolationList( tuple.getT4() );
        this.setPersonImage( tuple.getT2() );
        this.setPinpp( tuple.getT1() ); }

    // for PINFL request
    public PsychologyCard ( final Tuple2< Pinpp, String > tuple ) {
        this.setPersonImage( tuple.getT2() );
        this.setPinpp( tuple.getT1() ); }
}