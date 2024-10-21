package com.ssd.mvd.inspectors;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import reactor.util.retry.Retry;
import java.time.Duration;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public class RetryInspector extends WebFluxInspector {
    @EntityConstructorAnnotation( permission = SerDes.class )
    protected <T extends UuidInspector> RetryInspector( @lombok.NonNull final Class<T> instance ) {
        super( RetryInspector.class );

        AnnotationInspector.checkCallerPermission( instance, RetryInspector.class );
        AnnotationInspector.checkAnnotationIsImmutable( RetryInspector.class );
    }

    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    protected final synchronized <T> Retry retry (
            @lombok.NonNull final EntityCommonMethods<T> entityCommonMethods
    ) {
        return Retry.backoff( 2, Duration.ofSeconds( 1 ) )
                .doBeforeRetry( retrySignal -> super.logging( retrySignal, entityCommonMethods.getMethodName() ) )
                .doAfterRetry( retrySignal -> super.logging( entityCommonMethods.getMethodName(), retrySignal ) )
                .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() );
    }
}
