package com.ssd.mvd.inspectors;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import org.apache.commons.collections4.list.UnmodifiableList;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.*;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public class CollectionsInspector extends TimeInspector {
    protected CollectionsInspector () {
        super( CollectionsInspector.class );
    }

    @EntityConstructorAnnotation( permission = CustomServiceCleaner.class )
    protected <T extends UuidInspector> CollectionsInspector( @lombok.NonNull final Class<T> instance ) {
        super( CollectionsInspector.class );

        AnnotationInspector.checkCallerPermission( instance, CollectionsInspector.class );
        AnnotationInspector.checkAnnotationIsImmutable( CollectionsInspector.class );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized <T> List<T> emptyList () {
        return new UnmodifiableList<>( Collections.emptyList() );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized <T> List<T> newList () {
        return new CopyOnWriteArrayList<>();
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected static synchronized <T, V> WeakHashMap<T, V> newMap () {
        return new WeakHashMap<>( 1 );
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
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected static synchronized <T> List<T> convertArrayToList (
            @lombok.NonNull final T[] objects
    ) {
        return UnmodifiableList.unmodifiableList( Arrays.asList( objects ) );
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected static synchronized <T> boolean isCollectionNotEmpty (
            final Collection<T> collection
    ) {
        return collection != null && !collection.isEmpty();
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> true" )
    protected static synchronized <T, U> boolean isCollectionNotEmpty (
            final Map<T, U> map
    ) {
        return map != null && !map.isEmpty();
    }

    @lombok.Synchronized
    public static synchronized <T> void checkAndClear (
            final Collection<T> collection
    ) {
        if ( isCollectionNotEmpty( collection ) ) collection.clear();
    }

    @lombok.Synchronized
    protected static synchronized <T, U> void checkAndClear (
            final Map<T, U> map
    ) {
        if ( isCollectionNotEmpty( map ) ) map.clear();
    }
}
