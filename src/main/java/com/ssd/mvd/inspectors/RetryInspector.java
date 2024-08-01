package com.ssd.mvd.inspectors;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import reactor.util.retry.Retry;
import java.time.Duration;

public class RetryInspector extends WebFluxInspector {
    protected final synchronized <T> Retry retry (
            final EntityCommonMethods<T> entityCommonMethods
    ) {
        return Retry.backoff( 2, Duration.ofSeconds( 1 ) )
                .doBeforeRetry( retrySignal -> super.logging( retrySignal, entityCommonMethods.getMethodName() ) )
                .doAfterRetry( retrySignal -> super.logging( entityCommonMethods.getMethodName(), retrySignal ) )
                .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() );
    }
}
