package com.ssd.mvd.entity;

import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.modelForCadastr.ModelForCadastor;
import com.ssd.mvd.entity.modelForGai.ModelForCar;
import com.ssd.mvd.entity.modelForGai.ModelForGai;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PsychologyCard {
    private String pinpp;
    private final ModelForCar modelForCar;
    private final ModelForGai modelForGai;
    private final ModelForPassport modelForPassport;
    private final ModelForCadastor modelForCadastor;
}
