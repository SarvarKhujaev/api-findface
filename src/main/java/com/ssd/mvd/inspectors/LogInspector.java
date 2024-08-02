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
    private final static Logger LOGGER = LogManager.getLogger( "LOGGER_WITH_JSON_LAYOUT" );

    protected final synchronized void logging ( final Object o ) {
        LOGGER.info( o.getClass().getName() + " was closed successfully at: " + super.newDate() );
    }

    protected final synchronized <T> void logging (
            final Throwable throwable,
            final EntityCommonMethods<T> entityCommonMethods,
            final String params
    ) {
        LOGGER.error( "Error in {}: {}", entityCommonMethods.getMethodName(), throwable );

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
        LOGGER.info( clazz.getName() + " was created at: " + super.newDate() );
    }

    protected final synchronized void logging (
            final Retry.RetrySignal retrySignal,
            final Methods methods
    ) {
        LOGGER.info( "Retrying in {} has started {}: ", methods, retrySignal );
    }

    protected  final synchronized void logging (
            final Methods methods,
            final Retry.RetrySignal retrySignal
    ) {
        LOGGER.info( "Retrying in {} has finished {}: ", methods, retrySignal );
    }

    // log on error
    protected  final synchronized void logging (
            final Methods method,
            final Object o
    ) {
        LOGGER.info( "Method {} has completed successfully {}", method, o );
    }

    // log on subscribe
    protected  final synchronized void logging ( final Throwable error ) {
        LOGGER.error( "Error: " + error.getMessage() );
    }

    protected  final synchronized void logging ( final String method ) {
        LOGGER.info( method + " has subscribed" );
    }

    // сохраняем логи о пользователе который отправил запрос на сервис
    protected final BiFunction< PsychologyCard, ApiResponseModel, PsychologyCard > saveUserUsageLog =
            ( psychologyCard, apiResponseModel ) -> {
                KafkaDataControl
                        .getKafkaDataControl()
                        .sendMessage(
                                new UserRequest(
                                    psychologyCard,
                                    apiResponseModel
                                )
                        );

                return psychologyCard;
            };
}
