package com.ssd.mvd.entity.boardCrossing;

import java.util.List;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class Data {
    private Person person;
    private List< CrossBoard > crossBoardList;

    public Data( final List< CrossBoard > data, final Person person ) {
        this.setCrossBoardList( data );
        this.setPerson( person ); }
}
