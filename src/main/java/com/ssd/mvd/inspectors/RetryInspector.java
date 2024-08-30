package com.ssd.mvd.inspectors;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import reactor.util.retry.Retry;
import java.time.Duration;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public class RetryInspector extends WebFluxInspector {
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    protected final synchronized <T extends StringOperations> Retry retry (
            @lombok.NonNull final EntityCommonMethods<T> entityCommonMethods
    ) {
        return Retry.backoff( 2, Duration.ofSeconds( 1 ) )
                .doBeforeRetry( retrySignal -> super.logging( retrySignal, entityCommonMethods.getMethodName() ) )
                .doAfterRetry( retrySignal -> super.logging( entityCommonMethods.getMethodName(), retrySignal ) )
                .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() );
    }
}
