package com.ssd.mvd.inspectors;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.*;

public class TimeInspector extends StringOperations {
    public final static Duration HttpClientDuration = Duration.ofSeconds( 20 );

    protected final synchronized Date newDate () {
        return new Date();
    }

    protected final synchronized Date parseStringIntoDate (
            final String value
    ) throws ParseException {
        return new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).parse( value );
    }
}
