package com.ssd.mvd.inspectors;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.entityForLogging.ErrorLog;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.kafka.KafkaDataControl;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import reactor.core.publisher.Mono;

public class ErrorController extends CustomSerializer {
    protected final Supplier< ErrorResponse > getErrorResponse = () -> {
            SerDes.getSerDes().getUpdateTokens().get();
            return this.error.apply( "", Errors.GAI_TOKEN_ERROR );
    };

    protected final BiFunction< String, Errors, ErrorResponse > error = ( error, errors ) -> ErrorResponse
            .builder()
            .message( errors.getErrorMEssage( error ) )
            .errors( errors )
            .build();

    // saves error from external service
    protected final BiFunction< String, Methods, Mono< ? > > saveErrorLog = ( errorMessage, methods ) -> {
        KafkaDataControl
                .getKafkaDataControl()
                .sendMessage( new ErrorLog ( errorMessage ) );

        return super.convert(
                methods.getEntityWithError(
                        this.error.apply(
                                errorMessage,
                                Errors.EXTERNAL_SERVICE_500_ERROR
                        )
                )
        );
    };


    // логирует любые ошибки
    protected final synchronized void saveErrorLog (
            final Methods methodName,
            final String params,
            final String reason
    ) {
        KafkaDataControl
                .getKafkaDataControl()
                .sendMessage(
                        EntitiesInstances.NOTIFICATION
                                .setPinfl( params )
                                .setReason( reason )
                                .setMethodName( methodName.name() )
                                .setCallingTime( super.newDate() )
                );
    }

    // отправляет ошибку на сервис Шамсиддина, в случае если какой - либо сервис не отвечает
    protected final synchronized void saveErrorLog (
            final String errorMessage
    ) {
        KafkaDataControl
                .getKafkaDataControl()
                .sendMessage( new ErrorLog ( errorMessage ) );
    }

    protected final synchronized <T extends StringOperations> T completeError (
            final EntityCommonMethods<T> entitiesInstances
    ) {
        return entitiesInstances.generate(
                Errors.SERVICE_WORK_ERROR.name(),
                Errors.SERVICE_WORK_ERROR
        );
    }

    protected final synchronized <U extends Exception> Mono< String > completeError (
            final U exception,
            final Errors errors
    ) {
        return super.convert(
                String.join(
                        " : ",
                        errors.name(),
                        exception.getMessage()
                )
        );
    }

    protected final synchronized <T extends StringOperations, U extends Exception> Mono< T > completeError (
            final U exception,
            final EntityCommonMethods<T> entityCommonMethods
    ) {
        return super.convert(
                entityCommonMethods.generate(
                        entityCommonMethods.getMethodName().name(),
                        exception instanceof IllegalArgumentException
                                ? Errors.TOO_MANY_RETRIES_ERROR
                                : Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                )
        );
    }
}
