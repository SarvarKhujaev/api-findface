package com.ssd.mvd.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.ssd.mvd.constants.Methods;
import reactor.util.retry.Retry;

@lombok.Data
public class LogInspector {
    private final static LogInspector INSTANCE = new LogInspector();
    private final Logger LOGGER = LogManager.getLogger( "LOGGER_WITH_JSON_LAYOUT" );

    public static LogInspector getInstance () { return INSTANCE; }

    // log on error
    public void logging (
            Throwable throwable,
            Methods method,
            String params ) {
        this.getLOGGER().error( "Error in {}: {}", method, throwable );
        ErrorController
                .getInstance()
                .saveErrorLog(
                        method.name(),
                        params,
                        "Error: " + throwable.getMessage() );
        ErrorController
                .getInstance()
                .saveErrorLog( throwable.getMessage() ); }

    public void logging ( Retry.RetrySignal retrySignal, Methods methods ) {
        this.getLOGGER().info( "Retrying in {} has started {}: ", methods, retrySignal ); }

    public void logging ( Methods methods, Retry.RetrySignal retrySignal ) {
        this.getLOGGER().info( "Retrying in {} has finished {}: ", methods, retrySignal ); }

    // log on error
    public void logging ( Methods method, Object o ) { this.getLOGGER().info( "Method {} has completed successfully {}", method, o ); }

    // log on subscribe
    public void logging ( String method ) { this.getLOGGER().info( method + " has subscribed" ); }
}
