package com.ssd.mvd.entity.boardCrossing;

import java.util.List;

public final class Data {
    public Person getPerson() {
        return this.person;
    }

    public void setPerson( final Person person ) {
        this.person = person;
    }

    public List< CrossBoard > getCrossBoardList() {
        return this.crossBoardList;
    }

    public void setCrossBoardList( final List< CrossBoard > crossBoardList ) {
        this.crossBoardList = crossBoardList;
    }

    private Person person;
    private List< CrossBoard > crossBoardList;

    public static Data generate (
            final List< CrossBoard > data,
            final Person person
    ) {
        return new Data( data, person );
    }

    private Data(
            final List< CrossBoard > data,
            final Person person
    ) {
        this.setCrossBoardList( data );
        this.setPerson( person );
    }
}
