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

    public ModelForCarList setErrorResponse ( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    public List< ModelForCar > getModelForCarList() {
        return this.modelForCarList;
    }

    public ModelForCarList setModelForCarList (
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
    public ModelForCarList generate(
            final String message,
            final Errors errors
    ) {
        return this.generate(
                super.error.apply(
                        message,
                        errors
                )
        );
    }

    @Override
    public ModelForCarList generate (
            final ErrorResponse errorResponse
    ) {
        return this.generate().setErrorResponse( errorResponse );
    }

    @Override
    public ModelForCarList generate() {
        return new ModelForCarList();
    }

    @Override
    public Methods getMethodName() {
        return Methods.GET_MODEL_FOR_CAR_LIST;
    }

    @Override
    public String getMethodApi() {
        return super.getAPI_FOR_MODEL_FOR_CAR_LIST();
    }

    @Override
    public ModelForCarList generate(
            final String response
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
