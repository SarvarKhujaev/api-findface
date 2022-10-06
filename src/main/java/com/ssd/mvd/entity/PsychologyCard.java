package com.ssd.mvd.entity;

import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.foreigner.Foreigner;
import com.ssd.mvd.entity.family.Family;

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
    private com.ssd.mvd.entity.modelForPassport.Data modelForPassport;

    public PsychologyCard ( Results results ) {
        this.setPapilonData( results.getResults() );
        this.setViolationList( results.getViolationList() ); }
}
