package com.ssd.mvd.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.entity.modelForGai.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarTotalData {
    private Double confidence; // процент достоверности
    private Boolean matched;

    private String cameraImage; // image which was made by camera
    private String gosNumber;
    private String brand; // brand of car ( Toyota, Chevrolet )
    private String color;
    private String type; // Spark, Nexia
    private String id;

    @JsonDeserialize
    private Tonirovka tonirovka;
    @JsonDeserialize
    private Insurance insurance;
    @JsonDeserialize
    private TexPassport texPassport; // where to take link
    @JsonDeserialize
    private ModelForCar modelForCar;
    @JsonDeserialize
    private PsychologyCard psychologyCard;
    @JsonDeserialize
    private ViolationsList violationsList;
    @JsonDeserialize
    private DoverennostList doverennostList;
}
