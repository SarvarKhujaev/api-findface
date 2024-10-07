package com.ssd.mvd.inspectors;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public class WebFluxInspector extends Config {
    protected static final int RESULT_COUNT = 1000;

    @lombok.NonNull
    @lombok.Synchronized
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
    @lombok.Synchronized
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

    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    protected static synchronized Flux< Integer > convertValuesToParallelFlux (
            @lombok.NonNull final Consumer< Integer > customConsumer
    ) {
        return Flux.range( 0, RESULT_COUNT )
                .parallel( 20 )
                .runOn( Schedulers.parallel() )
                .map( integer -> {
                    customConsumer.accept( integer );
                    return integer;
                } )
                .sequential()
                .publishOn( Schedulers.parallel() );
    }
}
