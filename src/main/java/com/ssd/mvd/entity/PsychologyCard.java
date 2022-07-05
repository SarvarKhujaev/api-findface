package com.ssd.mvd.entity;

import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.modelForCadastr.ModelForCadastor;
import lombok.Builder;
import lombok.Data;

@Data
public class PsychologyCard {
    private Pinpp pinpp;
    private PapilonData papilonData;
    private ModelForAddress modelForAddress;
    private ModelForCarList modelForCarList; // the list of all cars which belongs to this person
    private ModelForPassport modelForPassport;
    private com.ssd.mvd.entity.modelForCadastr.Data modelForCadastor;
}
