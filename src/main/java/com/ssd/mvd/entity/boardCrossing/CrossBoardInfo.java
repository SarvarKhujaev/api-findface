package com.ssd.mvd.entity.boardCrossing;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.inspectors.*;

import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;

import java.util.List;

public final class CrossBoardInfo extends CollectionsInspector implements EntityCommonMethods< CrossBoardInfo > {
    public List< com.ssd.mvd.entity.boardCrossing.Data > getData() {
        return this.Data;
    }

    public String getResult() {
        return this.Result;
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    private CrossBoardInfo setData( final List< com.ssd.mvd.entity.boardCrossing.Data > data ) {
        this.Data = data;
        return this;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    public CrossBoardInfo setErrorResponse( @lombok.NonNull final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    private String Result;
    private List< Data > Data;
    private ErrorResponse errorResponse;

    @EntityConstructorAnnotation
    public <T> CrossBoardInfo ( @lombok.NonNull final Class<T> instance ) {
        AnnotationInspector.checkCallerPermission( instance, CrossBoardInfo.class );
        AnnotationInspector.checkAnnotationIsImmutable( CrossBoardInfo.class );
    }

    private CrossBoardInfo () {}

    private CrossBoardInfo (
            @lombok.NonNull final List< CrossBoard > crossBoards,
            @lombok.NonNull final Person person
    ) {
        this.setData( super.newList() )
                .getData().add( com.ssd.mvd.entity.boardCrossing.Data.generate( crossBoards, person ) );
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    public CrossBoardInfo generate (
            @lombok.NonNull final String response
    ) {
        return new CrossBoardInfo(
                response.contains( "[{\"card_id" )
                        ? CustomSerializer.stringToArrayList(
                                response.substring( response.indexOf( "[{\"card_id" ), response.length() - 3 ),
                                CrossBoard[].class
                        )
                        : super.emptyList(),
                response.contains( "transaction_id" )
                        ? EntitiesInstances.PERSON.get().generate( response )
                        : EntitiesInstances.PERSON.get()
        );
    }

    @Override
    @lombok.NonNull
    public CrossBoardInfo generate () {
        return new CrossBoardInfo();
    }

    @Override
    @lombok.NonNull
    public Methods getMethodName() {
        return Methods.GET_CROSS_BOARDING;
    }

    @Override
    public void close() {
        super.analyze(
                this.getData(),
                CustomServiceCleaner::clearReference
        );

        checkAndClear( this.getData() );
    }
}
