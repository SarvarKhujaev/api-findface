package com.ssd.mvd.entity;

import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.modelForGai.DoverennostList;
import com.ssd.mvd.entity.modelForGai.Insurance;
import com.ssd.mvd.entity.modelForGai.Tonirovka;
import lombok.NoArgsConstructor;
import java.util.List;
import lombok.Data;

@Data
@NoArgsConstructor
public class PsychologyCard {
    private Pinpp pinpp;
    private String personImage; // the image of the person

    private List< Insurance > insurance;
    private List< Tonirovka > tonirovka;
    private List< DoverennostList > doverennostList;

    private List< PapilonData > papilonData;
    private List< Violation > violationList;

    private ModelForCarList modelForCarList; // the list of all cars which belongs to this person
    private ModelForAddress modelForAddress;

    private com.ssd.mvd.entity.modelForCadastr.Data modelForCadastr;
    private com.ssd.mvd.entity.modelForPassport.Data modelForPassport;

    public PsychologyCard ( Results value ) {
        this.setPapilonData( value.getResults() );
        this.setViolationList( value.getViolationList() ); }
}
