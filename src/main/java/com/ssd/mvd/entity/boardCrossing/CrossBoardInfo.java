package com.ssd.mvd.entity.boardCrossing;

import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.inspectors.ErrorController;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Errors;

import java.util.List;

public final class CrossBoardInfo
        extends ErrorController
        implements EntityCommonMethods< CrossBoardInfo >, ServiceCommonMethods {
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

    public String getResult() {
        return this.Result;
    }

    private String Result;
    private List< Data > Data;
    private ErrorResponse errorResponse;

    public CrossBoardInfo () {}

    private CrossBoardInfo ( final ErrorResponse errorResponse ) {
        this.setErrorResponse( errorResponse );
    }

    public static CrossBoardInfo generate (
            final List< CrossBoard > crossBoards,
            final Person person
    ) {
        return new CrossBoardInfo( crossBoards, person );
    }

    private CrossBoardInfo (
            final List< CrossBoard > crossBoards,
            final Person person
    ) {
        this.setData( super.newList() );
        this.getData().add( com.ssd.mvd.entity.boardCrossing.Data.generate( crossBoards, person ) );
    }

    @Override
    public CrossBoardInfo generate(
            final String message,
            final Errors errors
    ) {
        return new CrossBoardInfo().generate(
                super.error.apply(
                        message,
                        errors
                )
        );
    }

    @Override
    public CrossBoardInfo generate (
            final ErrorResponse errorResponse
    ) {
        return new CrossBoardInfo( errorResponse );
    }

    @Override
    public CrossBoardInfo generate () {
        return new CrossBoardInfo();
    }

    @Override
    public void close() {
        super.analyze(
                this.getData(),
                com.ssd.mvd.entity.boardCrossing.Data::close
        );

        this.getData().clear();
    }
}
