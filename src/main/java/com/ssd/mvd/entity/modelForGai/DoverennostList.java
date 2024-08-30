package com.ssd.mvd.entity.modelForGai;

import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.inspectors.Config;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;

import java.util.List;

public final class DoverennostList
        extends Config
        implements EntityCommonMethods< DoverennostList >, ServiceCommonMethods {
    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public DoverennostList setErrorResponse( @lombok.NonNull final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    public List< Doverennost > getDoverennostsList() {
        return this.doverennostsList;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    private DoverennostList setDoverennostsList( final List< Doverennost > doverennostsList ) {
        this.doverennostsList = doverennostsList;
        return this;
    }

    private ErrorResponse errorResponse;
    private List< Doverennost > doverennostsList;

    public DoverennostList () {}

    @Override
    @lombok.NonNull
    public DoverennostList generate() {
        return new DoverennostList();
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public DoverennostList generate (
            @lombok.NonNull final ErrorResponse errorResponse
    ) {
        return this.setErrorResponse( errorResponse );
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _ -> this" )
    public DoverennostList generate(
            @lombok.NonNull final String message,
            @lombok.NonNull final Errors errors
    ) {
        return this.generate().setErrorResponse(
                super.error.apply(
                        message,
                        errors
                )
        );
    }

    @Override
    @lombok.NonNull
    public Methods getMethodName() {
        return Methods.GET_DOVERENNOST_LIST;
    }

    @Override
    @lombok.NonNull
    public String getMethodApi() {
        return super.getAPI_FOR_DOVERENNOST_LIST();
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    public DoverennostList generate(
            @lombok.NonNull final String response
    ) {
        return this.generate().setDoverennostsList( super.stringToArrayList( response, Doverennost[].class ) );
    }

    @Override
    public void close() {
        this.getDoverennostsList().clear();
    }
}
