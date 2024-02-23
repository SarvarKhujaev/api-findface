package com.ssd.mvd.controller;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

public class CollectionsInspector extends TimeInspector {
    protected CollectionsInspector () {}

    protected <T> List<T> emptyList () {
        return Collections.emptyList();
    }

    public <T> ArrayList<T> newList () {
        return new ArrayList<>();
    }

    protected <T, V> TreeMap<T, V> newTreeMap () {
        return new TreeMap<>();
    }

    protected <T, V> Map<T, V> newMap () {
        return new HashMap<>();
    }

    protected boolean checkCollectionsLengthEquality (
            final Map firstCollection,
            final Collection secondCollection ) {
        return firstCollection.size() == secondCollection.size();
    }

    public <T> void analyze (
            final Collection<T> someList,
            final Consumer<T> someConsumer ) {
        someList.forEach( someConsumer );
    }

    protected <T, V> void analyze (
            final Map< T, V > someList,
            final BiConsumer<T, V> someConsumer ) {
        someList.forEach( someConsumer );
    }

    protected <T> boolean isCollectionNotEmpty ( final Collection<T> collection ) {
        return collection != null && !collection.isEmpty();
    }

    protected <T> List<T> convertArrayToList (
            final T[] objects
    ) {
        return Arrays.asList( objects );
    }
}
