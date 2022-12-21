package com.ssd.mvd.entity;

import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.entity.modelForGai.*;

import reactor.util.function.Tuple5;
import reactor.core.publisher.Mono;

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

    public CarTotalData ( Tuple5<
            Mono< Tonirovka >,
            Mono< ModelForCar >,
            Mono< DoverennostList >,
            Mono< Insurance >,
            Mono< ViolationsList > > tuple ) {
tuple.getT1().subscribe( this::setTonirovka );
tuple.getT4().subscribe( this::setInsurance );
tuple.getT2().subscribe( this::setModelForCar );
tuple.getT5().subscribe( this::setViolationsList );
tuple.getT3().subscribe( this::setDoverennostList ); }

    public CarTotalData( ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }
}
