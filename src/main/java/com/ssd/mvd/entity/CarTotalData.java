package com.ssd.mvd.entity;

import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.entity.modelForGai.*;
import reactor.util.function.Tuple5;

@lombok.Data
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

    private ErrorResponse errorResponse;

    public CarTotalData save ( PsychologyCard psychologyCard ) {
        this.setPsychologyCard( psychologyCard );
        return this; }

    public CarTotalData ( ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }

    public CarTotalData ( Tuple5<
            Tonirovka,
            ModelForCar,
            DoverennostList,
            Insurance,
            ViolationsList > objects ) {
        this.setDoverennostList( objects.getT3() );
        this.setViolationsList( objects.getT5() );
        this.setModelForCar( objects.getT2() );
        this.setTonirovka( objects.getT1() );
        this.setInsurance( objects.getT4() ); }
}
