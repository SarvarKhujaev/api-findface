package com.ssd.mvd.entity.boardCrossing;

import com.ssd.mvd.controller.CollectionsInspector;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.constants.ErrorResponse;

import java.util.List;

public final class CrossBoardInfo extends CollectionsInspector implements EntityCommonMethods< CrossBoardInfo > {
    public List< com.ssd.mvd.entity.boardCrossing.Data > getData() {
        return this.Data;
    }

    public void setData( final List< com.ssd.mvd.entity.boardCrossing.Data > data ) {
        this.Data = data;
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    public void setErrorResponse( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
    }

    private String Result;
    private List< Data > Data;
    private ErrorResponse errorResponse;

    public static CrossBoardInfo generate (
            final List< CrossBoard > crossBoards,
            final Person person
    ) {
        return new CrossBoardInfo( crossBoards, person );
    }

    @Override
    public CrossBoardInfo generate (
            final ErrorResponse errorResponse
    ) {
        return new CrossBoardInfo( errorResponse );
    }

    private CrossBoardInfo (
            final List< CrossBoard > crossBoards,
            final Person person
    ) {
        this.setData( super.newList() );
        this.getData().add( com.ssd.mvd.entity.boardCrossing.Data.generate( crossBoards, person ) );
    }

    private CrossBoardInfo ( final ErrorResponse errorResponse ) {
        this.setErrorResponse( errorResponse );
    }

    public CrossBoardInfo () {}
}
