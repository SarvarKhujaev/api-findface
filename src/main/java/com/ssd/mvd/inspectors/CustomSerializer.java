package com.ssd.mvd.inspectors;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.interfaces.EntityCommonMethods;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

import java.util.List;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public class CustomSerializer extends DataValidationInspector {
    @EntityConstructorAnnotation( permission = ErrorController.class )
    protected <T extends UuidInspector> CustomSerializer( @lombok.NonNull final Class<T> instance ) {
        super( CustomSerializer.class );

        AnnotationInspector.checkCallerPermission( instance, CustomSerializer.class );
        AnnotationInspector.checkAnnotationIsImmutable( CustomSerializer.class );
    }

    private final static Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    protected CustomSerializer () {}

    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    public static synchronized <T> String serialize ( @lombok.NonNull final T object ) {
        return gson.toJson( object );
    }

    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_, _ -> !null" )
    public static synchronized <T extends EntityCommonMethods< ? > > T deserialize (
            @lombok.NonNull final String value,
            @lombok.NonNull final Class<T> clazz
    ) {
        return gson.fromJson( value, clazz );
    }

    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_, _ -> !null" )
    public static synchronized <T> List<T> stringToArrayList (
            @lombok.NonNull final String object,
            @lombok.NonNull final Class< T[] > clazz
    ) {
        return convertArrayToList( gson.fromJson( object, clazz ) );
    }
}
