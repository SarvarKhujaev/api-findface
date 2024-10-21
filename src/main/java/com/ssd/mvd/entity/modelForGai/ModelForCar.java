package com.ssd.mvd.entity.modelForGai;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.annotations.WeakReferenceAnnotation;
import com.ssd.mvd.inspectors.AnnotationInspector;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.inspectors.CustomSerializer;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.entity.ModelForCarList;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.constants.Methods;

import reactor.util.function.Tuple3;

public final class ModelForCar implements EntityCommonMethods< ModelForCar > {
    public String getPinpp() {
        return this.Pinpp;
    }

    public void setPinpp ( final String pinpp ) {
        this.Pinpp = pinpp;
    }

    public String getPerson() {
        return this.Person;
    }

    public void setPerson ( final String person ) {
        this.Person = person;
    }

    public String getPlateNumber() {
        return this.PlateNumber;
    }

    public void setTonirovka ( final Tonirovka tonirovka ) {
        this.tonirovka = tonirovka;
    }

    public void setInsurance ( final Insurance insurance ) {
        this.insurance = insurance;
    }

    public void setDoverennostList ( final DoverennostList doverennostList  ) {
        this.doverennostList = doverennostList;
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    public String getStir() {
        return this.Stir;
    }

    public String getYear() {
        return this.Year;
    }

    public String getModel() {
        return this.Model;
    }

    public String getColor() {
        return this.Color;
    }

    public String getKuzov() {
        return this.Kuzov;
    }

    public String getPower() {
        return this.Power;
    }

    public String getSeats() {
        return this.Seats;
    }

    public String getEngine() {
        return this.Engine;
    }

    public String getStands() {
        return this.Stands;
    }

    public String getAddress() {
        return this.Address;
    }

    public String getFuelType() {
        return this.FuelType;
    }

    public String getAdditional() {
        return this.Additional;
    }

    public String getFullWeight() {
        return this.FullWeight;
    }

    public String getVehicleType() {
        return this.VehicleType;
    }

    public String getEmptyWeight() {
        return this.EmptyWeight;
    }

    public String getOrganization() {
        return this.Organization;
    }

    public String getRegistrationDate() {
        return this.RegistrationDate;
    }

    public String getTexPassportSerialNumber() {
        return this.TexPassportSerialNumber;
    }

    public Tonirovka getTonirovka() {
        return this.tonirovka;
    }

    public Insurance getInsurance() {
        return this.insurance;
    }

    public DoverennostList getDoverennostList() {
        return this.doverennostList;
    }

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

    @WeakReferenceAnnotation( name = "tonirovka", isCollection = false )
    private Tonirovka tonirovka;
    @WeakReferenceAnnotation( name = "insurance", isCollection = false )
    private Insurance insurance;
    @WeakReferenceAnnotation( name = "doverennostList", isCollection = false )
    private DoverennostList doverennostList;

    @WeakReferenceAnnotation( name = "errorResponse", isCollection = false )
    private ErrorResponse errorResponse;

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public ModelForCar setErrorResponse ( @lombok.NonNull final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _ -> !null" )
    public PsychologyCard save (
            @lombok.NonNull final Tuple3<
                Insurance,
                Tonirovka,
                DoverennostList > tuple3,
            @lombok.NonNull final PsychologyCard psychologyCard
    ) {
        this.setDoverennostList( tuple3.getT3() );
        this.setInsurance( tuple3.getT1() );
        this.setTonirovka( tuple3.getT2() );
        return psychologyCard;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _ -> !null" )
    public ModelForCarList save (
            @lombok.NonNull final Tuple3<
                    Insurance,
                    Tonirovka,
                    DoverennostList > tuple3,
            @lombok.NonNull final ModelForCarList modelForCarList
    ) {
        this.setDoverennostList( tuple3.getT3() );
        this.setInsurance( tuple3.getT1() );
        this.setTonirovka( tuple3.getT2() );

        return modelForCarList;
    }

    @EntityConstructorAnnotation
    public <T> ModelForCar ( @lombok.NonNull final Class<T> instance ) {
        AnnotationInspector.checkCallerPermission( instance, ModelForCar.class );
        AnnotationInspector.checkAnnotationIsImmutable( ModelForCar.class );
    }

    private ModelForCar () {}

    @Override
    @lombok.NonNull
    public ModelForCar generate() {
        return new ModelForCar();
    }

    @Override
    @lombok.NonNull
    public Methods getMethodName() {
        return Methods.GET_MODEL_FOR_CAR_LIST;
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public ModelForCar generate(
            @lombok.NonNull final String response
    ) {
        return CustomSerializer.deserialize( response, this.getClass() );
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public ModelForCar generate (
            @lombok.NonNull final ErrorResponse errorResponse
    ) {
        return this.setErrorResponse( errorResponse );
    }
}