package com.ssd.mvd.entity;

import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.entity.modelForGai.*;

import reactor.util.function.Tuple5;
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

    private ErrorResponse errorResponse;

    public CarTotalData ( ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }

    public CarTotalData ( Tuple5< Tonirovka, ModelForCar, DoverennostList, Insurance, ViolationsList > objects ) {
        this.setDoverennostList( objects.getT3() );
        this.setViolationsList( objects.getT5() );
        this.setModelForCar( objects.getT2() );
        this.setTonirovka( objects.getT1() );
        this.setInsurance( objects.getT4() ); }
}
