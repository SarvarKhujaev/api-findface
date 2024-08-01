package com.ssd.mvd.inspectors;

import com.ssd.mvd.entity.Pinpp;

public class StringOperations {
    public final static String EMPTY = "";

    protected final synchronized String joinString ( final Pinpp pinpp ) {
        return String.join( " ", pinpp.getName(), pinpp.getSurname(), pinpp.getPatronym() );
    }
}
