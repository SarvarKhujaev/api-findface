package com.ssd.mvd.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.ssd.mvd.constants.Methods;
import reactor.util.retry.Retry;

public class LogInspector extends ErrorController {
    private final Logger LOGGER = LogManager.getLogger( "LOGGER_WITH_JSON_LAYOUT" );

    public Logger getLOGGER() { return LOGGER; }

    // log on error
    public void logging (
            final Throwable throwable,
            final Methods method,
            final String params ) {
        this.getLOGGER().error( "Error in {}: {}", method, throwable );
        super.saveErrorLog(
                method.name(),
                params,
                "Error: " + throwable.getMessage() );
        super.saveErrorLog( throwable.getMessage() ); }

    public void logging ( final Retry.RetrySignal retrySignal, final Methods methods ) {
        this.getLOGGER().info( "Retrying in {} has started {}: ", methods, retrySignal ); }

    public void logging ( final Methods methods, final Retry.RetrySignal retrySignal ) {
        this.getLOGGER().info( "Retrying in {} has finished {}: ", methods, retrySignal ); }

    // log on error
    public void logging ( final Methods method, final Object o ) { this.getLOGGER().info( "Method {} has completed successfully {}", method, o ); }

    // log on subscribe
    public void logging ( final String method ) { this.getLOGGER().info( method + " has subscribed" ); }
}
