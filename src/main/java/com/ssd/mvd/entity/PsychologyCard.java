package com.ssd.mvd.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PsychologyCard {
    private Pinpp pinpp;
    private String personImage; // the image of the person
    private List< PapilonData > papilonData;
    private List< Violation > violationList;
    private ModelForCarList modelForCarList; // the list of all cars which belongs to this person
    private com.ssd.mvd.entity.modelForCadastr.Data modelForCadastr;
    private com.ssd.mvd.entity.modelForPassport.Data modelForPassport;

    public PsychologyCard ( Results value ) { this.setPapilonData( value.getResults() ); }
}
