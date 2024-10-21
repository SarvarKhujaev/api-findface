package com.ssd.mvd.inspectors;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.subscribers.CustomSubscriber;
import com.ssd.mvd.constants.Methods;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.scheduling.annotation.Async;
import reactor.util.retry.Retry;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public class LogInspector extends ErrorController {
    protected LogInspector () {
        super( LogInspector.class );
    }

    @EntityConstructorAnnotation(
            permission = {
                    Config.class,
                    CustomSubscriber.class,
            }
    )
    protected <T extends UuidInspector> LogInspector ( @lombok.NonNull final Class<T> instance ) {
        super( LogInspector.class );

        AnnotationInspector.checkCallerPermission( instance, LogInspector.class );
        AnnotationInspector.checkAnnotationIsImmutable( LogInspector.class );
    }

    private final static Logger LOGGER = LogManager.getLogger( "LOGGER_WITH_JSON_LAYOUT" );

    @Async( value = "logging" )
    @lombok.Synchronized
    protected synchronized void logging ( @lombok.NonNull final Object o ) {
        LOGGER.info(
                String.join(
                        EMPTY,
                        o.getClass().getName(),
                        " was closed successfully at: ",
                        super.newDate().get().toString()
                )
        );
    }

    @Async( value = " extends" )
    @lombok.Synchronized
    protected synchronized <T> void logging (
            @lombok.NonNull final Throwable throwable,
            @lombok.NonNull final EntityCommonMethods<T> entityCommonMethods,
            @lombok.NonNull final String params
    ) {
        LOGGER.error(
                String.join(
                        EMPTY,
                        "Error in {}: {}",
                        entityCommonMethods.getMethodName().name(),
                        throwable.getMessage()
                )
        );

        super.saveErrorLog(
                entityCommonMethods.getMethodName(),
                params,
                "Error: " + throwable.getMessage()
        );

        super.saveErrorLog( throwable.getMessage() );
    }

    @Async( value = "logging" )
    @lombok.Synchronized
    protected synchronized void logging (
            @lombok.NonNull final Class<?> clazz
    ) {
        LOGGER.info(
                String.join(
                        EMPTY,
                        clazz.getName(),
                        " was created at: ",
                        super.newDate().get().toString()
                )
        );
    }

    @Async( value = "logging" )
    @lombok.Synchronized
    protected synchronized void logging (
            @lombok.NonNull final Retry.RetrySignal retrySignal,
            @lombok.NonNull final Methods methods
    ) {
        LOGGER.info(
                String.join(
                        EMPTY,
                        "Retrying in {} has started {}: ",
                        methods.name(),
                        retrySignal.failure().getMessage()
                )
        );
    }

    @Async( value = "logging" )
    @lombok.Synchronized
    protected synchronized void logging (
            @lombok.NonNull final Methods methods,
            @lombok.NonNull final Retry.RetrySignal retrySignal
    ) {
        LOGGER.info(
                String.join(
                        EMPTY,
                        "Retrying in {} has finished {}: ",
                        methods.name(),
                        String.valueOf( retrySignal.totalRetries() )
                )
        );
    }

    // log on error
    @Async( value = "logging" )
    @lombok.Synchronized
    protected synchronized void logging (
            @lombok.NonNull final Methods method,
            @lombok.NonNull final Object o
    ) {
        LOGGER.info(
                String.join(
                        EMPTY,
                        "Method {} has completed successfully {}",
                        method.name(),
                        o.getClass().getName()
                )
        );
    }

    // log on subscribe
    @Async( value = "logging" )
    @lombok.Synchronized
    protected synchronized void logging ( @lombok.NonNull final Throwable error ) {
        LOGGER.error(
                String.join(
                        EMPTY,
                        "Error: ",
                        error.getMessage()
                )
        );
    }

    @Async( value = "logging" )
    @lombok.Synchronized
    protected synchronized void logging ( @lombok.NonNull final String method ) {
        LOGGER.info(
                String.join(
                        EMPTY,
                        method,
                        " has subscribed"
                )
        );
    }
}
