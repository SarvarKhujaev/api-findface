package com.ssd.mvd.entity.modelForGai;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.constants.ErrorResponse;

import java.util.List;

public final class DoverennostList implements EntityCommonMethods< DoverennostList > {
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
}
