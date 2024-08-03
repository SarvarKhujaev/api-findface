package com.ssd.mvd.inspectors;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.constants.Methods;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import reactor.util.retry.Retry;

public class LogInspector extends ErrorController {
    private final static Logger LOGGER = LogManager.getLogger( "LOGGER_WITH_JSON_LAYOUT" );

    protected final synchronized void logging ( final Object o ) {
        LOGGER.info(
                String.join(
                        EMPTY,
                        o.getClass().getName(),
                        " was closed successfully at: ",
                        super.newDate().toString()
                )
        );
    }

    protected final synchronized <T> void logging (
            final Throwable throwable,
            final EntityCommonMethods<T> entityCommonMethods,
            final String params
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

    protected final synchronized void logging (
            final Class<?> clazz
    ) {
        LOGGER.info(
                String.join(
                        EMPTY,
                        clazz.getName(),
                        " was created at: ",
                        super.newDate().toString()
                )
        );
    }

    protected final synchronized void logging (
            final Retry.RetrySignal retrySignal,
            final Methods methods
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

    protected  final synchronized void logging (
            final Methods methods,
            final Retry.RetrySignal retrySignal
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
    protected  final synchronized void logging (
            final Methods method,
            final Object o
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
    protected  final synchronized void logging ( final Throwable error ) {
        LOGGER.error(
                String.join(
                        EMPTY,
                        "Error: ",
                        error.getMessage()
                )
        );
    }

    protected  final synchronized void logging ( final String method ) {
        LOGGER.info(
                String.join(
                        EMPTY,
                        method,
                        " has subscribed"
                )
        );
    }
}
