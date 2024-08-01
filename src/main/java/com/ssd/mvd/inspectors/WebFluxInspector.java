package com.ssd.mvd.inspectors;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class WebFluxInspector extends Config {
    protected final synchronized <T, U> Flux< U > convertValuesToParallelFluxWithMap (
            final Collection< T > collection,
            final Function< T, U > customFunction
    ) {
        return Flux.fromStream(
                        collection.stream()
                ).parallel( super.checkDifference( collection.size() ) )
                .runOn( Schedulers.parallel() )
                .map( customFunction )
                .sequential()
                .publishOn( Schedulers.single() );
    }

    protected final synchronized <T> Flux< T > convertValuesToParallelFluxWithFilter(
            final Collection< T > collection,
            final Predicate< T > customPredicate
    ) {
        return Flux.fromStream(
                        collection.stream()
                ).parallel( super.checkDifference( collection.size() ) )
                .runOn( Schedulers.parallel() )
                .filter( customPredicate )
                .sequential()
                .publishOn( Schedulers.single() );
    }
}
