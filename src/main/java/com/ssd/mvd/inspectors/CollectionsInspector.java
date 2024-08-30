package com.ssd.mvd.inspectors;

import java.util.function.Consumer;
import java.util.*;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public class CollectionsInspector extends TimeInspector {
    protected CollectionsInspector () {}

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized <T> List<T> emptyList () {
        return Collections.emptyList();
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized <T> ArrayList<T> newList () {
        return new ArrayList<>();
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected static synchronized <T, V> Map<T, V> newMap () {
        return new HashMap<>();
    }

    @lombok.Synchronized
    protected final synchronized <T> void analyze (
            @lombok.NonNull final Collection<T> someList,
            @lombok.NonNull final Consumer<T> someConsumer
    ) {
        someList.forEach( someConsumer );
    }

    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    protected final synchronized <T> List<T> convertArrayToList (
            @lombok.NonNull final T[] objects
    ) {
        return Arrays.asList( objects );
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized <T> boolean isCollectionNotEmpty (
            final Collection<T> collection
    ) {
        return collection != null && !collection.isEmpty();
    }
}
