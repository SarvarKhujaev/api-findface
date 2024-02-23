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
import java.util.function.Supplier;

import reactor.core.publisher.Mono;

public class ErrorController extends DataValidationInspector {
    private final Notification notification = new Notification();

    private Notification getNotification() {
        return this.notification;
    }

    protected final Supplier< ErrorResponse > getErrorResponse = () -> {
            SerDes.getSerDes().getUpdateTokens().get();
            return this.error.apply( "", 6 );
    };

    protected final BiFunction< String, Integer, ErrorResponse > error = ( error, value ) -> ErrorResponse
            .builder()
            .message( switch ( value ) {
                case 1 -> "Error in external service: " + error;
                case 2 -> "Service error: " + error;
                case 3 -> "Data for: " + error + " was not found";
                case 4 -> "Connection Error: " + error;
                case 6 -> "GAI token is unavailable";
                default -> "Service: " + error + " does not return response!!!";
            } )
            .errors( switch ( value ) {
                case 1 -> Errors.EXTERNAL_SERVICE_500_ERROR; // используется когда внешние сервисы возвращают 500 ошибку
                case 2 -> Errors.SERVICE_WORK_ERROR; // используется когда сам сервис ловит ошибку при выполнении
                case 3 -> Errors.DATA_NOT_FOUND; // используется когда сервис возвращает пустое тело при запросе
                case 4 -> Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED;
                case 6 -> Errors.GAI_TOKEN_ERROR;
                default -> Errors.TOO_MANY_RETRIES_ERROR;
            } )
            .build();

    // логирует любые ошибки
    protected void saveErrorLog (
            final String methodName,
            final String params,
            final String reason ) {
        this.getNotification().setPinfl( params );
        this.getNotification().setReason( reason );
        this.getNotification().setMethodName( methodName );
        this.getNotification().setCallingTime( super.newDate() );

        KafkaDataControl
                .getInstance()
                .getWriteErrorLog()
                .accept( SerDes
                        .getSerDes()
                        .getGson()
                        .toJson( this.getNotification() ) );
    }

    // отправляет ошибку на сервис Шамсиддина, в случае если какой - либо сервис не отвечает
    protected void saveErrorLog ( final String errorMessage ) {
        KafkaDataControl
                .getInstance()
                .getWriteToKafkaErrorLog()
                .accept( SerDes
                        .getSerDes()
                        .getGson()
                        .toJson( ErrorLog
                                .builder()
                                .errorMessage( errorMessage )
                                .createdAt( super.newDate().getTime() )
                                .integratedService( IntegratedServiceApis.OVIR.getName() )
                                .integratedServiceApiDescription( IntegratedServiceApis.OVIR.getDescription() )
                                .build() ) );
    }

    // saves error from external service
    protected final BiFunction< String, Methods, Mono< ? > > saveErrorLog = ( errorMessage, methods ) -> {
            KafkaDataControl
                    .getInstance()
                    .getWriteToKafkaErrorLog()
                    .accept( SerDes
                            .getSerDes()
                            .getGson()
                            .toJson( ErrorLog
                                    .builder()
                                    .errorMessage( errorMessage )
                                    .createdAt( super.newDate().getTime() )
                                    .integratedService( IntegratedServiceApis.OVIR.getName() )
                                    .integratedServiceApiDescription( IntegratedServiceApis.OVIR.getDescription() )
                                    .build() ) );

            return super.convert( switch ( methods ) {
                case GET_MODEL_FOR_CAR_LIST -> ModelForCarList.generate( this.error.apply( errorMessage, 1 ) );
                case GET_MODEL_FOR_ADDRESS -> ModelForAddress.generate( this.error.apply( errorMessage, 1 ) );
                case GET_DOVERENNOST_LIST -> DoverennostList.generate( this.error.apply( errorMessage, 1 ) );
                case GET_VIOLATION_LIST -> ViolationsList.generate( this.error.apply( errorMessage, 1 ) );
                case GET_VEHILE_DATA -> ModelForCar.generate( this.error.apply( errorMessage, 1 ) );
                case GET_INSURANCE -> Insurance.generate( this.error.apply( errorMessage, 1 ) );
                case GET_TONIROVKA -> Tonirovka.generate( this.error.apply( errorMessage, 1 ) );
                case GET_PINPP -> Pinpp.generate( this.error.apply( errorMessage, 1 ) );
                case CADASTER -> Data.generate( this.error.apply( errorMessage, 1 ) );
                case GET_MODEL_FOR_PASSPORT -> com.ssd.mvd.entity.modelForPassport.ModelForPassport.generate(
                        this.error.apply( errorMessage, 1 )
                );
                default -> Errors.EXTERNAL_SERVICE_500_ERROR.name();
            } );
    };
}
