package com.ssd.mvd.controller;

import java.util.function.Consumer;
import java.util.*;

public class CollectionsInspector extends TimeInspector {
    protected CollectionsInspector () {}

    protected final synchronized <T> List<T> emptyList () {
        return Collections.emptyList();
    }

    protected final synchronized <T> ArrayList<T> newList () {
        return new ArrayList<>();
    }

    protected final synchronized <T, V> Map<T, V> newMap () {
        return new HashMap<>();
    }

    protected final synchronized <T> void analyze (
            final Collection<T> someList,
            final Consumer<T> someConsumer
    ) {
        someList.forEach( someConsumer );
    }

    protected final synchronized <T> List<T> convertArrayToList (
            final T[] objects
    ) {
        return Arrays.asList( objects );
    }

    protected final synchronized <T> boolean isCollectionNotEmpty (
            final Collection<T> collection
    ) {
        return collection != null && !collection.isEmpty();
    }
}
