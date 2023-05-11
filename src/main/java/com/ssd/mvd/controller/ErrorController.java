package com.ssd.mvd.controller;

import com.ssd.mvd.entityForLogging.IntegratedServiceApis;
import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.modelForCadastr.Data;
import com.ssd.mvd.entityForLogging.ErrorLog;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.kafka.KafkaDataControl;
import com.ssd.mvd.entity.ModelForCarList;
import com.ssd.mvd.entity.modelForGai.*;
import com.ssd.mvd.kafka.Notification;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.entity.Pinpp;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import reactor.core.publisher.Mono;
import java.util.Date;

public class ErrorController extends DataValidationInspector {
    private final Notification notification = new Notification();

    public final Supplier< ErrorResponse > getErrorResponse = () -> {
            SerDes.getSerDes().getUpdateTokens().get();
            return ErrorResponse
                    .builder()
                    .message( "GAI token is unavailable" )
                    .errors( Errors.GAI_TOKEN_ERROR )
                    .build(); };

    // используется когда внешние сервисы возвращают 500 ошибку
    public final Function< String, ErrorResponse > getExternalServiceErrorResponse = error -> ErrorResponse
            .builder()
            .message( "Error in external service: " + error )
            .errors( Errors.EXTERNAL_SERVICE_500_ERROR )
            .build();

    // используется когда сам сервис ловит ошибку при выполнении
    public final Function< String, ErrorResponse > getServiceErrorResponse = error -> ErrorResponse
            .builder()
            .message( "Service error: " + error )
            .errors( Errors.SERVICE_WORK_ERROR )
            .build();

    // используется когда сервис возвращает пустое тело при запросе
    public final Function< String, ErrorResponse > getDataNotFoundErrorResponse = error -> ErrorResponse
            .builder()
            .message( "Data for: " + error + " was not found" )
            .errors( Errors.DATA_NOT_FOUND )
            .build();

    public final Function< Throwable, ErrorResponse > getConnectionError = error -> ErrorResponse
            .builder()
            .message( "Connection Error: " + error )
            .errors( Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED )
            .build();

    public final Function<Methods, ErrorResponse > getTooManyRetriesError = methods -> ErrorResponse
            .builder()
            .message( "Service: " + methods + " does not return response!!!" )
            .errors( Errors.TOO_MANY_RETRIES_ERROR )
            .build();

    private Notification getNotification() { return this.notification; }

    // логирует любые ошибки
    public void saveErrorLog (
            final String methodName,
            final String params,
            final String reason ) {
        this.getNotification().setPinfl( params );
        this.getNotification().setReason( reason );
        this.getNotification().setMethodName( methodName );
        this.getNotification().setCallingTime( new Date() );
        KafkaDataControl
                .getInstance()
                .getWriteErrorLog()
                .accept( SerDes
                        .getSerDes()
                        .getGson()
                        .toJson( this.getNotification() ) ); }

    // отправляет ошибку на сервис Шамсиддина, в случае если какой - либо сервис не отвечает
    public void saveErrorLog ( final String errorMessage ) {
        KafkaDataControl
                .getInstance()
                .getWriteToKafkaErrorLog()
                .accept( SerDes
                        .getSerDes()
                        .getGson()
                        .toJson( ErrorLog
                                .builder()
                                .errorMessage( errorMessage )
                                .createdAt( new Date().getTime() )
                                .integratedService( IntegratedServiceApis.OVIR.getName() )
                                .integratedServiceApiDescription( IntegratedServiceApis.OVIR.getDescription() )
                                .build() ) ); }

    // saves error from external service
    public final BiFunction< String, Methods, Mono< ? > > saveErrorLog = ( errorMessage, methods ) -> {
            KafkaDataControl
                    .getInstance()
                    .getWriteToKafkaErrorLog()
                    .accept( SerDes
                            .getSerDes()
                            .getGson()
                            .toJson( ErrorLog
                                    .builder()
                                    .errorMessage( errorMessage )
                                    .createdAt( new Date().getTime() )
                                    .integratedService( IntegratedServiceApis.OVIR.getName() )
                                    .integratedServiceApiDescription( IntegratedServiceApis.OVIR.getDescription() )
                                    .build() ) );
            return Mono.just( switch ( methods ) {
                case GET_MODEL_FOR_CAR_LIST -> new ModelForCarList( this.getExternalServiceErrorResponse.apply( errorMessage ) );
                case GET_MODEL_FOR_ADDRESS -> new ModelForAddress( this.getExternalServiceErrorResponse.apply( errorMessage ) );
                case GET_DOVERENNOST_LIST -> new DoverennostList( this.getExternalServiceErrorResponse.apply( errorMessage ) );
                case GET_VIOLATION_LIST -> new ViolationsList( this.getExternalServiceErrorResponse.apply( errorMessage ) );
                case GET_VEHILE_DATA -> new ModelForCar( this.getExternalServiceErrorResponse.apply( errorMessage ) );
                case GET_INSURANCE -> new Insurance( this.getExternalServiceErrorResponse.apply( errorMessage ) );
                case GET_TONIROVKA -> new Tonirovka( this.getExternalServiceErrorResponse.apply( errorMessage ) );
                case GET_PINPP -> new Pinpp( this.getExternalServiceErrorResponse.apply( errorMessage ) );
                case CADASTER -> new Data( this.getExternalServiceErrorResponse.apply( errorMessage ) );
                case GET_MODEL_FOR_PASSPORT -> new com.ssd.mvd.entity.modelForPassport.ModelForPassport(
                        this.getExternalServiceErrorResponse.apply( errorMessage ) );
                default -> Errors.EXTERNAL_SERVICE_500_ERROR.name(); } ); };
}
