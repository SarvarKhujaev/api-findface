package com.ssd.mvd.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.annotations.WeakReferenceAnnotation;

import com.ssd.mvd.inspectors.DataValidationInspector;
import com.ssd.mvd.inspectors.CollectionsInspector;
import com.ssd.mvd.inspectors.AnnotationInspector;
import com.ssd.mvd.inspectors.CustomSerializer;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.entity.modelForGai.ModelForCar;

import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;

import java.util.List;

public final class ModelForCarList extends CollectionsInspector implements EntityCommonMethods< ModelForCarList > {
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

    @WeakReferenceAnnotation( name = "errorResponse", isCollection = false )
    private ErrorResponse errorResponse;
    @JsonDeserialize
    @WeakReferenceAnnotation( name = "modelForCarList" )
    private List< ModelForCar > modelForCarList;

    @EntityConstructorAnnotation
    public <T> ModelForCarList ( @lombok.NonNull final Class<T> instance ) {
        AnnotationInspector.checkCallerPermission( instance, ModelForCarList.class );
        AnnotationInspector.checkAnnotationIsImmutable( ModelForCarList.class );
    }

    public ModelForCarList () {}

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
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public ModelForCarList generate(
            @lombok.NonNull final String response
    ) {
        return this.generate().setModelForCarList( CustomSerializer.stringToArrayList( response, ModelForCar[].class ) );
    }

    @Override
    public void close() {
        super.analyze(
                this.getModelForCarList(),
                modelForCar -> {
                    if (
                            DataValidationInspector.objectIsNotNull(
                                    modelForCar.getDoverennostList()
                            )
                    ) {
                        CollectionsInspector.checkAndClear( modelForCar.getDoverennostList().getDoverennostsList() );
                    }
                }
        );

        checkAndClear( this.getModelForCarList() );
    }
}
