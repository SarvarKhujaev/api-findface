package com.ssd.mvd.inspectors;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.entityForLogging.UserRequest;
import com.ssd.mvd.entityForLogging.ErrorLog;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.entity.ApiResponseModel;
import com.ssd.mvd.kafka.KafkaDataControl;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.springframework.scheduling.annotation.Async;
import reactor.core.publisher.Mono;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public class ErrorController extends CustomSerializer {
    protected final Supplier< ErrorResponse > getErrorResponse = () -> {
        SerDes.getSerDes().getUpdateTokens().get();
        return this.error.apply( SPACE, Errors.GAI_TOKEN_ERROR );
    };

    protected final BiFunction< String, Errors, ErrorResponse > error = ( error, errors ) -> ErrorResponse
            .builder()
            .message( errors.getErrorMEssage( error ) )
            .errors( errors )
            .build();

    @SuppressWarnings(
            value = "saves error from external service"
    )
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _ -> !null" )
    protected final synchronized <T extends StringOperations> Mono<T> saveErrorLog (
            @lombok.NonNull final String errorMessage,
            @lombok.NonNull final EntityCommonMethods<T> entityCommonMethods
    ) {
        KafkaDataControl
                .getKafkaDataControl()
                .sendMessage( new ErrorLog ( errorMessage ) );

        return super.convert(
                entityCommonMethods.generate().generate(
                        this.error.apply(
                                errorMessage,
                                Errors.EXTERNAL_SERVICE_500_ERROR
                        )
                )
        );
    }


    @SuppressWarnings(
            value = "логирует любые ошибки"
    )
    @Async( value = "saveErrorLog" )
    @lombok.Synchronized
    protected synchronized void saveErrorLog (
            @lombok.NonNull final Methods methodName,
            @lombok.NonNull final String params,
            @lombok.NonNull final String reason
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

    @SuppressWarnings(
            value = "отправляет ошибку на сервис Шамсиддина, в случае если какой - либо сервис не отвечает"
    )
    @Async( value = "saveErrorLog" )
    @lombok.Synchronized
    protected synchronized void saveErrorLog (
            @lombok.NonNull final String errorMessage
    ) {
        KafkaDataControl
                .getKafkaDataControl()
                .sendMessage( new ErrorLog ( errorMessage ) );
    }

    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    protected final synchronized <T extends StringOperations> T completeError (
            @lombok.NonNull final EntityCommonMethods<T> entitiesInstances
    ) {
        return entitiesInstances.generate(
                Errors.SERVICE_WORK_ERROR.name(),
                Errors.SERVICE_WORK_ERROR
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_, _ -> !null" )
    protected final synchronized <U extends Exception> Mono< String > completeError (
            @lombok.NonNull final U exception,
            @lombok.NonNull final Errors errors
    ) {
        return super.convert(
                String.join(
                        SPACE_WITH_DOUBLE_DOTS,
                        errors.name(),
                        exception.getMessage()
                )
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_, _ -> !null" )
    protected final synchronized <T extends StringOperations, U extends Exception> Mono< T > completeError (
            @lombok.NonNull final U exception,
            @lombok.NonNull final EntityCommonMethods<T> entityCommonMethods
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

    @SuppressWarnings(
            value = "сохраняем логи о пользователе который отправил запрос на сервис"
    )
    @lombok.NonNull
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
