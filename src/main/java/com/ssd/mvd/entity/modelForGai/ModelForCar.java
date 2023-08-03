package com.ssd.mvd.entity.modelForGai;

import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.entity.ModelForCarList;
import com.ssd.mvd.entity.PsychologyCard;
import reactor.util.function.Tuple3;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class ModelForCar {
    private String Stir;
    private String Year;
    private String Pinpp;
    private String Model;
    private String Color;
    private String Kuzov;
    private String Power;
    private String Seats;
    private String Person;
    private String Engine;
    private String Stands;
    private String Address;
    private String FuelType;
    private String Additional;
    private String FullWeight;
    private String VehicleType;
    private String EmptyWeight;
    private String PlateNumber;
    private String Organization;
    private String RegistrationDate;
    private String TexPassportSerialNumber;

    private Tonirovka tonirovka;
    private Insurance insurance;
    private DoverennostList doverennostList;

    private ErrorResponse errorResponse;

    public PsychologyCard save ( final Tuple3<
            Insurance,
            Tonirovka,
            DoverennostList > tuple3, PsychologyCard psychologyCard ) {
        this.setDoverennostList( tuple3.getT3() );
        this.setInsurance( tuple3.getT1() );
        this.setTonirovka( tuple3.getT2() );
        return psychologyCard; }

    public ModelForCarList save (
            final Tuple3<
                    Insurance,
                    Tonirovka,
                    DoverennostList > tuple3,
            final ModelForCarList modelForCarList ) {
        this.setDoverennostList( tuple3.getT3() );
        this.setInsurance( tuple3.getT1() );
        this.setTonirovka( tuple3.getT2() );
        return modelForCarList; }

    public ModelForCar ( final ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }
}