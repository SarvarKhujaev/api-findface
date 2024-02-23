package com.ssd.mvd.controller;

public class StringOperations {
    protected String splitWordAndJoin ( final String name ) {
        final String[] temp = name.split( " " );
        return temp.length > 3
                ? String.join( " ",
                temp[ 0 ].split( "/" )[1],
                temp[ 1 ].split( "/" )[1],
                temp[ 3 ].split( "/" )[1],
                temp[ 4 ] )
                : String.join( " ", temp );
    }

    public String concatNames ( final Object object ) {
        return String.join( "", String.valueOf( object ).split( "[.]" ) );
    }
}
