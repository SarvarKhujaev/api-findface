package com.ssd.mvd.entity;

import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.foreigner.Foreigner;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.entity.family.Family;

import reactor.util.function.Tuple6;
import reactor.util.function.Tuple5;
import reactor.util.function.Tuple4;
import reactor.core.publisher.Mono;

import lombok.NoArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PsychologyCard {
    private Pinpp pinpp;
    private String personImage; // the image of the person

    private String daddyPinfl;
    private String mommyPinfl;

    private Family daddyData;
    private Family mommyData;
    private Family childData;

    private List< PapilonData > papilonData;
    private List< Violation > violationList;
    private List< Foreigner > foreignerList;

    private ModelForCarList modelForCarList; // the list of all cars which belongs to this person
    private ModelForAddress modelForAddress;

    private com.ssd.mvd.entity.modelForCadastr.Data modelForCadastr;
    private com.ssd.mvd.entity.modelForPassport.Data modelForPassport;

    private ErrorResponse errorResponse;

    public PsychologyCard ( Results results ) {
        this.setPapilonData( results.getResults() );
        this.setViolationList( results.getViolationList() ); }

    public PsychologyCard ( ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }

    public PsychologyCard ( Results results,
                            Tuple4< Pinpp,
                                    com.ssd.mvd.entity.modelForCadastr.Data,
                                    String,
                                    ModelForCarList > tuple ) {
        this.setViolationList( results.getViolationList() );
        this.setPapilonData( results.getResults() );
        this.setModelForCadastr( tuple.getT2() );
        this.setModelForCarList( tuple.getT4() );
        this.setPersonImage( tuple.getT3() );
        this.setPinpp( tuple.getT1() ); }

    public PsychologyCard ( Tuple6<
            Pinpp,
            ModelForCarList,
            String,
            Mono< List >,
            Mono< Results >,
            ModelForAddress > tuple ) {
        tuple.getT4().subscribe( value -> this.setViolationList(
                value != null ? value : new ArrayList<>() ) );
        this.setModelForCarList( tuple.getT2() );
        this.setModelForAddress( tuple.getT6() );
        this.setPersonImage( tuple.getT3() );
        this.setPinpp( tuple.getT1() ); }

    public PsychologyCard( com.ssd.mvd.entity.modelForPassport.Data data,
                           Tuple6< Pinpp,
                                   String,
                                   ModelForCarList,
                                   ModelForAddress,
                                   Mono< List >,
                                   Mono< Results > > tuple ) {
        tuple.getT5().subscribe( value -> this.setViolationList( value != null ? value : new ArrayList<>() ) );
        this.setModelForAddress( tuple.getT4() );
        this.setModelForCarList( tuple.getT3() );
        this.setPersonImage( tuple.getT2() );
        this.setModelForPassport( data );
        this.setPinpp( tuple.getT1() ); }
}
