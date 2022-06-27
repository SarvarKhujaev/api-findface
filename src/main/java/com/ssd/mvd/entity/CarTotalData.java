package com.ssd.mvd.entity;

import com.ssd.mvd.constants.Status;
import com.ssd.mvd.entity.modelForGai.*;
import lombok.Builder;
import java.util.List;
import lombok.Data;

@Data
@Builder
public class CarTotalData {
    private String gosNumber;
    private String cameraImage; // image which was made by camera

    private Tonirovka tonirovka;
    private Insurance insurance;
    private TexPassport texPassport; // where to take link
    private ModelForCar modelForCar;
    private PsychologyCard psychologyCard;
    private ViolationsList violationsList;
    private DoverennostList doverennostList;

    private Status status;
    private List< String > patruls; // link to list of Patruls who is gonna deal with this Card
    private List< ReportForCard > reportForCards;
}
