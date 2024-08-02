package com.ssd.mvd.inspectors;

import com.ssd.mvd.entity.Pinpp;

public class StringOperations {
    public final static String EMPTY = "";

    protected final synchronized String checkString (
            final String value
    ) {
        return value != null && !value.isBlank()
                ? value
                : EMPTY;
    }

    protected final synchronized String joinString ( final Pinpp pinpp ) {
        return String.join(
                " ",
                this.checkString( pinpp.getName() ),
                this.checkString( pinpp.getSurname() ),
                this.checkString( pinpp.getPatronym() )
        );
    }
}
