package com.ssd.mvd.entity;

import com.ssd.mvd.entity.modelForGai.*;
import java.util.List;
import lombok.Data;

@Data
public class CarTotalData {
    private String gosNumber;
    private String cameraImage; // image which was made by camera

    private Tonirovka tonirovka;
    private Insurance insurance;
    private ModelForCar modelForCar;
    private PsychologyCard psychologyCard;
    private ViolationsList violationsList;
    private DoverennostList doverennostList;
    private ModelForCarList modelForCarList; // the list of all cars of each citizen

    private List< String > patruls; // link to list of Patruls who is gonna deal with this Card
    private List< ReportForCard > reportForCards;
}
