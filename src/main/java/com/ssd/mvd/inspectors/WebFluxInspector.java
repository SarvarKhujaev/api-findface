package com.ssd.mvd.inspectors;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class WebFluxInspector extends Config {
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _ -> _" )
    protected final synchronized <T, U> Flux< U > convertValuesToParallelFluxWithMap (
            @lombok.NonNull final Collection< T > collection,
            @lombok.NonNull final Function< T, U > customFunction
    ) {
        return Flux.fromStream( collection.stream() )
                .parallel( super.checkDifference( collection.size() ) )
                .runOn( Schedulers.parallel() )
                .map( customFunction )
                .sequential()
                .publishOn( Schedulers.single() );
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _ -> _" )
    protected final synchronized <T> Flux< T > convertValuesToParallelFluxWithFilter(
            @lombok.NonNull final Collection< T > collection,
            @lombok.NonNull final Predicate< T > customPredicate
    ) {
        return Flux.fromStream( collection.stream() )
                .parallel( super.checkDifference( collection.size() ) )
                .runOn( Schedulers.parallel() )
                .filter( customPredicate )
                .sequential()
                .publishOn( Schedulers.single() );
    }
}
