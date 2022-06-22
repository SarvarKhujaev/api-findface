package com.ssd.mvd.entity;

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

    private Tonirovka tonirovka;
    private Insurance insurance;
    private TexPassport texPassport; // where to take link
    private ModelForCar modelForCar;
    private PsychologyCard psychologyCard;
    private ViolationsList violationsList;
    private DoverennostList doverennostList;
}
