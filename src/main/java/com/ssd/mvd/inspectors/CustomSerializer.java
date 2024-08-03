package com.ssd.mvd.inspectors;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

import java.util.List;

public class CustomSerializer extends DataValidationInspector {
    private final static Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    protected CustomSerializer () {}

    private synchronized Gson getGson () {
        return gson;
    }

    protected final synchronized <T> String serialize ( final T object ) {
        return this.getGson().toJson( object );
    }

    protected final synchronized <T extends CustomSerializer> T deserialize (
            final String value,
            final Class<T> clazz
    ) {
        return this.getGson().fromJson( value, clazz );
    }

    protected final synchronized <T> List<T> stringToArrayList (
            final String object,
            final Class< T[] > clazz
    ) {
        return super.convertArrayToList( this.getGson().fromJson( object, clazz ) );
    }
}
