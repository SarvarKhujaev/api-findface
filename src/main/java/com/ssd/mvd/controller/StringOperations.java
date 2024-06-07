package com.ssd.mvd.controller;

import com.ssd.mvd.entity.Pinpp;

public class StringOperations {
    protected final synchronized String joinString ( final Pinpp pinpp ) {
        return String.join( " ", pinpp.getName(), pinpp.getSurname(), pinpp.getPatronym() );
    }
}
