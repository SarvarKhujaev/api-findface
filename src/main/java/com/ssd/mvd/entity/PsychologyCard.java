package com.ssd.mvd.entity;

import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.modelForCadastr.ModelForCadastor;
import lombok.Builder;
import lombok.Data;

@Data
public class PsychologyCard {
    private Pinpp pinpp;
    private ModelForPassport modelForPassport;
    private ModelForCadastor modelForCadastor;
}
