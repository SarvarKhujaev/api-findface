package com.ssd.mvd.entity.boardCrossing;

import com.ssd.mvd.interfaces.ServiceCommonMethods;
import java.util.List;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public final class Data implements ServiceCommonMethods {
    public Person getPerson() {
        return this.person;
    }

    public List< CrossBoard > getCrossBoardList() {
        return this.crossBoardList;
    }

    private Data setPerson( final Person person ) {
        this.person = person;
        return this;
    }

    private void setCrossBoardList( final List< CrossBoard > crossBoardList ) {
        this.crossBoardList = crossBoardList;
    }

    private Person person;
    private List< CrossBoard > crossBoardList;

    public static Data generate (
            @lombok.NonNull final List< CrossBoard > data,
            @lombok.NonNull final Person person
    ) {
        return new Data( data, person );
    }

    private Data(
            final List< CrossBoard > data,
            final Person person
    ) {
        this.setPerson( person ).setCrossBoardList( data );
    }

    @Override
    public void close() {
        this.getCrossBoardList().clear();
    }
}
