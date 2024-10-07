package com.ssd.mvd.inspectors;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.*;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public class TimeInspector extends StringOperations {
    public final static Duration HttpClientDuration = Duration.ofSeconds( 20 );

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized WeakReference< Date > newDate () {
        return EntitiesInstances.generateWeakEntity( new Date() );
    }

    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    public static synchronized Date parseStringIntoDate (
            @lombok.NonNull final String value
    ) {
        try {
            return new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).parse( value );
        } catch ( final ParseException e ) {
            throw new RuntimeException(e);
        }
    }
}
