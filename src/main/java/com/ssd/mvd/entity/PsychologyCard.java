package com.ssd.mvd.entity;

import lombok.Data;

import java.util.List;

@Data
public class PsychologyCard {
    private Pinpp pinpp;
    private List< PapilonData > papilonData;
    private ModelForCarList modelForCarList; // the list of all cars which belongs to this person
    private com.ssd.mvd.entity.modelForCadastr.Data modelForCadastr;
    private com.ssd.mvd.entity.modelForPassport.Data modelForPassport;
}
