package com.ssd.mvd.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.entity.modelForGai.ModelForCar;
import com.ssd.mvd.inspectors.ErrorController;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Errors;

import java.util.List;

public final class ModelForCarList
        extends ErrorController
        implements EntityCommonMethods< ModelForCarList >, ServiceCommonMethods {
    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    public void setErrorResponse ( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
    }

    public List< ModelForCar > getModelForCarList() {
        return this.modelForCarList;
    }

    public void setModelForCarList (
            final List< ModelForCar > modelForCarList
    ) {
        this.modelForCarList = modelForCarList;
    }

    private ErrorResponse errorResponse;
    @JsonDeserialize
    private List< ModelForCar > modelForCarList;

    public static ModelForCarList generate (
            final List< ModelForCar > modelForCarList
    ) {
        return new ModelForCarList( modelForCarList );
    }

    @Override
    public ModelForCarList generate(
            final String message,
            final Errors errors
    ) {
        return new ModelForCarList().generate(
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
        return new ModelForCarList( errorResponse );
    }

    private ModelForCarList ( final ErrorResponse errorResponse ) {
        this.setErrorResponse( errorResponse );
    }

    private ModelForCarList (
            final List< ModelForCar > modelForCarList
    ) {
        this.setModelForCarList( modelForCarList );
    }

    public ModelForCarList () {}

    @Override
    public void close() {
        this.getModelForCarList().clear();
        super.analyze(
                this.getModelForCarList(),
                modelForCar -> modelForCar.getDoverennostList().getDoverennostsList().clear()
        );
    }
}
