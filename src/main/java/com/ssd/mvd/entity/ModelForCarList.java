package com.ssd.mvd.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.entity.modelForGai.ModelForCar;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.inspectors.Config;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;

import java.util.List;

public final class ModelForCarList
        extends Config
        implements EntityCommonMethods< ModelForCarList >, ServiceCommonMethods {
    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public ModelForCarList setErrorResponse ( @lombok.NonNull final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    public List< ModelForCar > getModelForCarList() {
        return this.modelForCarList;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    private ModelForCarList setModelForCarList (
            final List< ModelForCar > modelForCarList
    ) {
        this.modelForCarList = modelForCarList;
        return this;
    }

    private ErrorResponse errorResponse;
    @JsonDeserialize
    private List< ModelForCar > modelForCarList;

    public ModelForCarList () {}

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _ -> this" )
    public ModelForCarList generate(
            @lombok.NonNull final String message,
            @lombok.NonNull final Errors errors
    ) {
        return this.generate(
                super.error.apply(
                        message,
                        errors
                )
        );
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public ModelForCarList generate (
            @lombok.NonNull final ErrorResponse errorResponse
    ) {
        return this.generate().setErrorResponse( errorResponse );
    }

    @Override
    @lombok.NonNull
    public ModelForCarList generate() {
        return new ModelForCarList();
    }

    @Override
    @lombok.NonNull
    public Methods getMethodName() {
        return Methods.GET_MODEL_FOR_CAR_LIST;
    }

    @Override
    @lombok.NonNull
    public String getMethodApi() {
        return super.getAPI_FOR_MODEL_FOR_CAR_LIST();
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public ModelForCarList generate(
            @lombok.NonNull final String response
    ) {
        return this.generate().setModelForCarList( this.stringToArrayList( response, ModelForCar[].class ) );
    }

    @Override
    public void close() {
        super.analyze(
                this.getModelForCarList(),
                modelForCar -> modelForCar.getDoverennostList().getDoverennostsList().clear()
        );

        this.getModelForCarList().clear();
    }
}
