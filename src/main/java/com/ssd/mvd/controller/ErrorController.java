package com.ssd.mvd.controller;

import com.ssd.mvd.entityForLogging.IntegratedServiceApis;
import com.ssd.mvd.entityForLogging.ErrorLog;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.kafka.KafkaDataControl;
import com.ssd.mvd.kafka.Notification;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import reactor.core.publisher.Mono;

public class ErrorController extends DataValidationInspector {
    private final Notification notification = new Notification();

    private Notification getNotification() {
        return this.notification;
    }

    protected final Supplier< ErrorResponse > getErrorResponse = () -> {
            SerDes.getSerDes().getUpdateTokens().get();
            return this.error.apply( "", Errors.GAI_TOKEN_ERROR );
    };

    protected final BiFunction< String, Errors, ErrorResponse > error = ( error, errors ) -> ErrorResponse
            .builder()
            .message( errors.getErrorMEssage( error ) )
            .errors( errors )
            .build();

    // логирует любые ошибки
    protected void saveErrorLog (
            final Methods methodName,
            final String params,
            final String reason
    ) {
        KafkaDataControl
                .getInstance()
                .writeErrorLog
                .accept(
                        SerDes
                            .getSerDes()
                            .getGson()
                            .toJson(
                                    this.getNotification()
                                            .setPinfl( params )
                                            .setReason( reason )
                                            .setMethodName( methodName.name() )
                                            .setCallingTime( super.newDate() )
                            )
                );
    }

    // отправляет ошибку на сервис Шамсиддина, в случае если какой - либо сервис не отвечает
    protected void saveErrorLog (
            final String errorMessage
    ) {
        KafkaDataControl
                .getInstance()
                .writeToKafkaErrorLog
                .accept(
                        SerDes
                            .getSerDes()
                            .getGson()
                            .toJson(
                                    ErrorLog
                                        .builder()
                                        .errorMessage( errorMessage )
                                        .createdAt( super.newDate().getTime() )
                                        .integratedService( IntegratedServiceApis.OVIR.getName() )
                                        .integratedServiceApiDescription( IntegratedServiceApis.OVIR.getDescription() )
                                        .build()
                            )
                );
    }

    // saves error from external service
    protected final BiFunction< String, Methods, Mono< ? > > saveErrorLog = ( errorMessage, methods ) -> {
            KafkaDataControl
                    .getInstance()
                    .writeToKafkaErrorLog
                    .accept(
                            SerDes
                                .getSerDes()
                                .getGson()
                                .toJson(
                                        ErrorLog
                                            .builder()
                                            .errorMessage( errorMessage )
                                            .createdAt( super.newDate().getTime() )
                                            .integratedService( IntegratedServiceApis.OVIR.getName() )
                                            .integratedServiceApiDescription( IntegratedServiceApis.OVIR.getDescription() )
                                            .build()
                                )
                    );

            return super.convert(
                    methods.getEntityWithError(
                            this.error.apply(
                                    errorMessage,
                                    Errors.EXTERNAL_SERVICE_500_ERROR
                            )
                    )
            );
    };
}
