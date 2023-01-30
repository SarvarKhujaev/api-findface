package com.ssd.mvd.entity;

import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.foreigner.Foreigner;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.entity.family.Family;

import reactor.util.function.Tuple6;
import reactor.util.function.Tuple5;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuple2;

import lombok.NoArgsConstructor;
import java.util.List;
import lombok.Data;

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
    private com.ssd.mvd.entity.modelForPassport.ModelForPassport modelForPassport;

    private ErrorResponse errorResponse;

    public PsychologyCard save ( com.ssd.mvd.entity.modelForCadastr.Data data ) {
        this.setModelForCadastr( data );
        return this; }

    public PsychologyCard save ( Tuple2<
            ModelForAddress,
            ModelForPassport > tuple2 ) {
        this.setModelForPassport( tuple2.getT2() );
        this.setModelForAddress( tuple2.getT1() );
        return this; }

    public PsychologyCard ( Results results ) {
        this.setPapilonData( results.getResults() );
        this.setViolationList( results.getViolationList() ); }

    public PsychologyCard ( ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }

    public PsychologyCard ( Results results,
                            Tuple3<
                                    Pinpp,
                                    String,
                                    ModelForCarList > tuple ) {
        this.setChildData( results.getChildData() );

        // личные данные матери, того чьи данные были переданы на данный сервис
        this.setMommyData( results.getMommyData() );
        this.setMommyPinfl( results.getMommyPinfl() );

        // личные данные отца, того чьи данные были переданы на данный сервис
        this.setDaddyData( results.getDaddyData() );
        this.setDaddyPinfl( results.getDaddyPinfl() );
        this.setViolationList( results.getViolationList() );
        this.setPapilonData( results.getResults() );
        this.setModelForCarList( tuple.getT3() );
        this.setPersonImage( tuple.getT2() );
        this.setPinpp( tuple.getT1() ); }

    public PsychologyCard ( com.ssd.mvd.entity.modelForPassport.ModelForPassport data,
                            Tuple6<
                                    Pinpp,
                                    String,
                                    ModelForCarList,
                                    ModelForAddress,
                                    List,
                                    Results > tuple ) {
        this.setChildData( tuple.getT6().getChildData() );

        // личные данные матери, того чьи данные были переданы на данный сервис
        this.setMommyData( tuple.getT6().getMommyData() );
        this.setMommyPinfl( tuple.getT6().getMommyPinfl() );

        // личные данные отца, того чьи данные были переданы на данный сервис
        this.setDaddyData( tuple.getT6().getDaddyData() );
        this.setDaddyPinfl( tuple.getT6().getDaddyPinfl() );

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
        this.setChildData( tuple.getT5().getChildData() );

        // личные данные матери, того чьи данные были переданы на данный сервис
        this.setMommyData( tuple.getT5().getMommyData() );
        this.setMommyPinfl( tuple.getT5().getMommyPinfl() );

        // личные данные отца, того чьи данные были переданы на данный сервис
        this.setDaddyData( tuple.getT5().getDaddyData() );
        this.setDaddyPinfl( tuple.getT5().getDaddyPinfl() );

        this.setModelForCarList( tuple.getT3() );
        this.setViolationList( tuple.getT4() );
        this.setPersonImage( tuple.getT2() );
        this.setPinpp( tuple.getT1() ); }
}