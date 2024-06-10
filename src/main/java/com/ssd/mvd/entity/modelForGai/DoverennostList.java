package com.ssd.mvd.entity.modelForGai;

import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.controller.ErrorController;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Errors;

import java.util.List;

public final class DoverennostList
        extends ErrorController
        implements EntityCommonMethods< DoverennostList >, ServiceCommonMethods {
    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    public void setErrorResponse( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
    }

    public List< Doverennost > getDoverennostsList() {
        return this.doverennostsList;
    }

    public void setDoverennostsList( final List< Doverennost > doverennostsList ) {
        this.doverennostsList = doverennostsList;
    }

    private ErrorResponse errorResponse;
    private List< Doverennost > doverennostsList;

    @Override
    public DoverennostList generate(
            final String message,
            final Errors errors
    ) {
        return new DoverennostList().generate(
                super.error.apply(
                        message,
                        errors
                )
        );
    }

    @Override
    public DoverennostList generate (
            final ErrorResponse errorResponse
    ) {
        return new DoverennostList( errorResponse );
    }

    public static DoverennostList generate (
            final List< Doverennost > doverennostsList
    ) {
        return new DoverennostList( doverennostsList );
    }

    private DoverennostList ( final ErrorResponse errorResponse ) {
        this.setErrorResponse( errorResponse );
    }

    private DoverennostList ( final List< Doverennost > doverennostsList ) {
        this.setDoverennostsList( doverennostsList );
    }

    public DoverennostList () {}

    @Override
    public void close() {
        this.getDoverennostsList().clear();
    }
}
