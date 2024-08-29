package com.ssd.mvd.inspectors;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.*;

public class TimeInspector extends StringOperations {
    public final static Duration HttpClientDuration = Duration.ofSeconds( 20 );

    @lombok.NonNull
    protected final synchronized Date newDate () {
        return new Date();
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized Date parseStringIntoDate (
            @lombok.NonNull final String value
    ) throws ParseException {
        return new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).parse( value );
    }
}
