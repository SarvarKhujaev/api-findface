package com.ssd.mvd.inspectors;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.entityForLogging.UserRequest;
import com.ssd.mvd.entity.ApiResponseModel;
import com.ssd.mvd.kafka.KafkaDataControl;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.constants.Methods;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiFunction;
import reactor.util.retry.Retry;

public class LogInspector extends ErrorController {
    private final Logger LOGGER = LogManager.getLogger( "LOGGER_WITH_JSON_LAYOUT" );

    private Logger getLOGGER() {
        return this.LOGGER;
    }

    protected final synchronized void logging ( final Object o ) {
        this.getLOGGER().info( o.getClass().getName() + " was closed successfully at: " + super.newDate() );
    }

    protected <T> void logging (
            final Throwable throwable,
            final EntityCommonMethods<T> entityCommonMethods,
            final String params
    ) {
        this.getLOGGER().error( "Error in {}: {}", entityCommonMethods.getMethodName(), throwable );

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
        this.getLOGGER().info( clazz.getName() + " was created at: " + super.newDate() );
    }

    protected void logging (
            final Retry.RetrySignal retrySignal,
            final Methods methods
    ) {
        this.getLOGGER().info( "Retrying in {} has started {}: ", methods, retrySignal );
    }

    protected void logging (
            final Methods methods,
            final Retry.RetrySignal retrySignal
    ) {
        this.getLOGGER().info( "Retrying in {} has finished {}: ", methods, retrySignal );
    }

    // log on error
    protected void logging (
            final Methods method,
            final Object o
    ) {
        this.getLOGGER().info( "Method {} has completed successfully {}", method, o );
    }

    // log on subscribe
    protected void logging ( final Throwable error ) {
        this.getLOGGER().error( "Error: " + error.getMessage() );
    }

    protected void logging ( final String method ) {
        this.getLOGGER().info( method + " has subscribed" );
    }

    // сохраняем логи о пользователе который отправил запрос на сервис
    protected final BiFunction< PsychologyCard, ApiResponseModel, PsychologyCard > saveUserUsageLog =
            ( psychologyCard, apiResponseModel ) -> {
                KafkaDataControl
                        .getInstance()
                        .writeToKafkaServiceUsage
                        .accept(
                                super.serialize(
                                            new UserRequest(
                                                psychologyCard,
                                                apiResponseModel
                                            )
                                    )
                        );

                return psychologyCard;
            };
}
