package com.ssd.mvd.inspectors;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.entityForLogging.ErrorLog;
import com.ssd.mvd.kafka.Notification;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.*;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public class TimeInspector extends StringOperations {
    protected TimeInspector () {
        super( TimeInspector.class );
    }

    @EntityConstructorAnnotation(
            permission = {
                    ErrorLog.class,
                    Notification.class,
                    CollectionsInspector.class
            }
    )
    protected <T extends UuidInspector> TimeInspector( @lombok.NonNull final Class<T> instance ) {
        super( TimeInspector.class );

        AnnotationInspector.checkCallerPermission( instance, TimeInspector.class );
        AnnotationInspector.checkAnnotationIsImmutable( TimeInspector.class );
    }

    protected final static Duration DURATION = Duration.ofMillis( 100 );
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
