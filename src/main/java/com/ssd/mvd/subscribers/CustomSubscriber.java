package com.ssd.mvd.subscribers;

import com.ssd.mvd.inspectors.LogInspector;

import org.reactivestreams.Subscription;
import org.reactivestreams.Subscriber;

import java.util.function.Consumer;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public final class CustomSubscriber<T> extends LogInspector implements Subscriber<T> {
    private final Consumer< T > objectConsumer;
    private Subscription subscription;

    public CustomSubscriber(
            @lombok.NonNull final Consumer< T > objectConsumer
    ) {
        super( CustomSubscriber.class );
        this.objectConsumer = objectConsumer;
    }

    @Override
    public void onSubscribe( @lombok.NonNull final Subscription subscription ) {
        this.subscription = subscription;
        this.subscription.request( 1 );
    }

    @Override
    public void onNext( @lombok.NonNull final T o ) {
        this.objectConsumer.accept( o );
        this.subscription.request( 1 );
    }

    @Override
    public void onError( @lombok.NonNull final Throwable throwable ) {
        super.logging( throwable );
    }

    @Override
    public void onComplete() {
        super.logging( this.getClass().getName() + " has completed its work" );
    }
}
