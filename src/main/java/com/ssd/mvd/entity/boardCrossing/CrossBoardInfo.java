package com.ssd.mvd.entity.boardCrossing;

import com.ssd.mvd.constants.ErrorResponse;

import java.util.ArrayList;
import java.util.List;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class CrossBoardInfo {
    private String Result;
    private List< Data > Data;
    private ErrorResponse errorResponse;

    public CrossBoardInfo ( final List< CrossBoard > crossBoards, final Person person ) {
        this.setData( new ArrayList<>() );
        this.getData().add( new Data( crossBoards, person ) ); }

    public CrossBoardInfo ( final ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }
}
