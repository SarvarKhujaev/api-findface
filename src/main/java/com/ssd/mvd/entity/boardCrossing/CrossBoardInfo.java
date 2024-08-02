package com.ssd.mvd.entity.boardCrossing;

import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.inspectors.Config;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;

import java.util.List;

public final class CrossBoardInfo
        extends Config
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

    public CrossBoardInfo setErrorResponse( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    public String getResult() {
        return this.Result;
    }

    private String Result;
    private List< Data > Data;
    private ErrorResponse errorResponse;

    public CrossBoardInfo () {}

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
        return this.setErrorResponse(
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
        return this.setErrorResponse( errorResponse );
    }

    @Override
    public CrossBoardInfo generate (
            final String response
    ) {
        return new CrossBoardInfo(
                response.contains( "[{\"card_id" )
                        ? super.stringToArrayList(
                                response.substring( response.indexOf( "[{\"card_id" ), response.length() - 3 ),
                                CrossBoard[].class
                        )
                        : super.emptyList(),
                response.contains( "transaction_id" )
                        ? super.deserialize(
                                response.substring( response.indexOf( "transaction_id" ) - 2, response.indexOf( "sex" ) + 9 ),
                                Person.class
                        )
                        : new Person()
        );
    }

    @Override
    public CrossBoardInfo generate () {
        return new CrossBoardInfo();
    }

    @Override
    public Methods getMethodName() {
        return Methods.GET_CROSS_BOARDING;
    }

    @Override
    public String getMethodApi() {
        return super.getAPI_FOR_BOARD_CROSSING();
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
