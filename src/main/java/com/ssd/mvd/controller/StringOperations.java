package com.ssd.mvd.controller;

import com.ssd.mvd.entity.Pinpp;

public class StringOperations {
    protected String joinString ( final Pinpp pinpp ) {
        return String.join( " ", pinpp.getName(), pinpp.getSurname(), pinpp.getPatronym() );
    }
}
