package com.ssd.mvd.inspectors;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

import java.util.List;

public class CustomSerializer extends DataValidationInspector {
    private final static Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    protected CustomSerializer () {}

    @lombok.NonNull
    private synchronized Gson getGson () {
        return gson;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    protected final synchronized <T> String serialize ( @lombok.NonNull final T object ) {
        return this.getGson().toJson( object );
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _ -> !null" )
    protected final synchronized <T extends CustomSerializer> T deserialize (
            @lombok.NonNull final String value,
            @lombok.NonNull final Class<T> clazz
    ) {
        return this.getGson().fromJson( value, clazz );
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _ -> !null" )
    protected final synchronized <T> List<T> stringToArrayList (
            @lombok.NonNull final String object,
            @lombok.NonNull final Class< T[] > clazz
    ) {
        return super.convertArrayToList( this.getGson().fromJson( object, clazz ) );
    }
}
