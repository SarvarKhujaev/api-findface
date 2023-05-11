package com.ssd.mvd.entity;

import reactor.util.function.Tuple5;
import com.ssd.mvd.entity.modelForGai.*;
import com.ssd.mvd.constants.ErrorResponse;

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

    public CarTotalData( final ModelForCar modelForCar ) { this.setModelForCar( modelForCar ); }

    public CarTotalData ( final ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }

    public CarTotalData save ( final PsychologyCard psychologyCard ) {
        this.setPsychologyCard( psychologyCard );
        return this; }

    public CarTotalData ( final Tuple5<
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
