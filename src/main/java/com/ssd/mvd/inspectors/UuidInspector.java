package com.ssd.mvd.inspectors;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.constants.Errors;

import java.lang.ref.WeakReference;
import java.util.UUID;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public class UuidInspector implements ServiceCommonMethods {
    protected final static WeakReference< UUID > uuid = EntitiesInstances.generateWeakEntity( generateTimeBased() );
    private final static String UUID_PATTERN = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";

    @EntityConstructorAnnotation( permission = StringOperations.class )
    protected <T extends UuidInspector> UuidInspector( @lombok.NonNull final Class<T> instance ) {
        AnnotationInspector.checkCallerPermission( instance, UuidInspector.class );
        AnnotationInspector.checkAnnotationIsImmutable( UuidInspector.class );
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    public static synchronized boolean isUUIDValid ( @lombok.NonNull final String object ) {
        return object.matches( UUID_PATTERN );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized UUID generateTimeBased () {
        return UUID.randomUUID();
    }

    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected static synchronized UUID convertFrom ( @lombok.NonNull final String value ) {
        return UUID.fromString( value );
    }

    @SuppressWarnings( value = "Prevent modification of the object's state" )
    @Override
    public UuidInspector clone() {
        throw new UnsupportedOperationException( Errors.OBJECT_IS_IMMUTABLE.translate( this.getClass().getName() ) );
    }
}
