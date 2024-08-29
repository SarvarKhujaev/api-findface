package com.ssd.mvd.inspectors;

import com.ssd.mvd.entity.Pinpp;

public class StringOperations {
    public final static String EMPTY = "";
    public final static String SPACE = " ";
    protected final static String SPACE_WITH_DOUBLE_DOTS = " : ";

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized String checkString (
            @lombok.NonNull final String value
    ) {
        return !value.isBlank()
                ? value
                : EMPTY;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized String joinString ( @lombok.NonNull final Pinpp pinpp ) {
        return String.join(
                SPACE,
                this.checkString( pinpp.getName() ),
                this.checkString( pinpp.getSurname() ),
                this.checkString( pinpp.getPatronym() )
        );
    }
}
