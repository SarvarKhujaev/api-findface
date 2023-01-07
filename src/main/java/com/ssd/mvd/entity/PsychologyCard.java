package com.ssd.mvd.entity;

import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.foreigner.Foreigner;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.entity.family.Family;

import reactor.util.function.Tuple6;
import reactor.util.function.Tuple5;
import reactor.util.function.Tuple3;

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
                            Tuple3<
                                    Pinpp,
                                    String,
                                    ModelForCarList > tuple ) {
        this.setViolationList( results.getViolationList() );
        this.setPapilonData( results.getResults() );
        this.setModelForCarList( tuple.getT3() );
        this.setPersonImage( tuple.getT2() );
        this.setPinpp( tuple.getT1() ); }

    public PsychologyCard ( com.ssd.mvd.entity.modelForPassport.Data data,
                           Tuple6<
                                   Pinpp,
                                   String,
                                   ModelForCarList,
                                   ModelForAddress,
                                   List,
                                   Results > tuple ) {
        this.setModelForAddress( tuple.getT4() );
        this.setModelForCarList( tuple.getT3() );
        this.setViolationList( tuple.getT5() );
        this.setPersonImage( tuple.getT2() );
        this.setModelForPassport( data );
        this.setModelForPassport( data );
        this.setPinpp( tuple.getT1() ); }

    public PsychologyCard ( Tuple5<
            Pinpp,
            String,
            ModelForCarList,
            List,
            Results > tuple ) {
        this.setModelForCarList( tuple.getT3() );
        this.setViolationList( tuple.getT4() );
        this.setPersonImage( tuple.getT2() );
        this.setPinpp( tuple.getT1() ); }
}
