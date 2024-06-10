package com.ssd.mvd.controller;

import java.util.*;
import java.time.Duration;
import java.util.function.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;

import reactor.util.retry.Retry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import io.netty.handler.logging.LogLevel;
import io.netty.channel.ConnectTimeoutException;

import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import com.google.gson.Gson;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.ssd.mvd.entity.*;
import com.ssd.mvd.request.*;
import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.entity.modelForGai.*;
import com.ssd.mvd.entity.foreigner.Foreigner;
import com.ssd.mvd.entity.boardCrossing.Person;
import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.entity.modelForCadastr.Data;
import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
import com.ssd.mvd.entity.boardCrossing.CrossBoard;
import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.entity.boardCrossing.CrossBoardInfo;
import com.ssd.mvd.publisher.CustomPublisherForRequest;
import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.modelForFioOfPerson.PersonTotalDataByFIO;

@lombok.Data
public final class SerDes extends Config implements ServiceCommonMethods {
    private Thread thread;
    private final Semaphore semaphore = new Semaphore( 1 );

    private final Gson gson = new Gson();
    private static SerDes serDes = new SerDes();
    private final HttpClient httpClient = HttpClient
            .create()
            .responseTimeout( Duration.ofSeconds( 20 ) )
            .headers( h -> h.add( "Content-Type", "application/json" ) )
            .wiretap(
                    "reactor.netty.http.client.HttpClient",
                    LogLevel.TRACE,
                    AdvancedByteBufFormat.TEXTUAL
            );

    public static SerDes getSerDes () {
        return serDes != null ? serDes : ( serDes = new SerDes() );
    }

    private synchronized <T> List<T> stringToArrayList (
            final String object,
            final Class< T[] > clazz
    ) {
        return super.convertArrayToList( this.getGson().fromJson( object, clazz ) );
    }

    private SerDes () {
        Unirest.setObjectMapper( new ObjectMapper() {
            private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

            @Override
            public String writeValue( Object o ) {
                try {
                    return this.objectMapper.writeValueAsString( o );
                } catch ( final JsonProcessingException e ) {
                    throw new RuntimeException( e );
                }
            }

            @Override
            public <T> T readValue( final String s, final Class<T> aClass ) {
                try {
                    return this.objectMapper.readValue( s, aClass );
                } catch ( final JsonProcessingException e ) {
                    throw new RuntimeException(e);
                }
            }
        } );

        super.getHeaders().put( "accept", "application/json" );
        this.setThread(
                new Thread(
                        () -> {
                            while ( this.getThread().isAlive() ) {
                                this.getUpdateTokens().get();
                                try {
                                    TimeUnit.MINUTES.sleep( super.getWaitingMins() );
                                    this.semaphore.release();
                                }
                                catch ( final InterruptedException e ) {
                                    this.close( e );
                                }
                            }
                        }
                )
        );
        this.getThread().setName( this.getClass().getName() );
        this.getThread().start();
        this.updateTokens.get();
    }

    private final Supplier< SerDes > updateTokens = () -> {
            super.logging( "Updating tokens..." );
            super.getFields().put( "Login", super.getLOGIN_FOR_GAI_TOKEN() );
            super.getFields().put( "Password" , super.getPASSWORD_FOR_GAI_TOKEN() );
            super.getFields().put( "CurrentSystem", super.getCURRENT_SYSTEM_FOR_GAI() );
            try {
                super.setTokenForGai(
                        String.valueOf(
                                Unirest.post( super.getAPI_FOR_GAI_TOKEN() )
                                    .fields( super.getFields() )
                                    .asJson()
                                    .getBody()
                                    .getObject()
                                    .get( "access_token" )
                        )
                );
                super.setTokenForPassport( super.getTokenForGai() );
                super.setWaitingMins( 180 );
                super.setFlag( true );
                return this;
            }
            catch ( final Exception e ) {
                super.setFlag( false );
                super.setWaitingMins( 3 );
                super.saveErrorLog( e.getMessage() );
                super.saveErrorLog( Methods.UPDATE_TOKENS, "access_token", "Error: " + e.getMessage() );
            }
            return this;
    };

    private final Function< String, Mono< CrossBoardInfo > > getCrossBoardInfo =
            SerialNumber -> this.getHttpClient()
                    .post()
                    .uri( super.getAPI_FOR_BOARD_CROSSING() )
                    .send(
                            ByteBufFlux.fromString(
                                    CustomPublisherForRequest.generate(
                                            SerialNumber,
                                            new RequestForBoardCrossing()
                                    )
                            )
                    ).responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                        case 401 -> this.getUpdateTokens().get().getGetCrossBoardInfo().apply( SerialNumber );
                        case 501 | 502 | 503 -> ( Mono< CrossBoardInfo > ) super.saveErrorLog.apply( res.status().toString(), Methods.GET_CROSS_BOARDING );
                        default -> super.checkResponse( res, content )
                                ? content
                                .asString()
                                .map( s -> CrossBoardInfo.generate(
                                        s.contains( "[{\"card_id" )
                                                ? this.stringToArrayList(
                                                        s.substring( s.indexOf( "[{\"card_id" ), s.length() - 3 ),
                                                        CrossBoard[].class
                                                )
                                                : super.emptyList(),
                                        s.contains( "transaction_id" )
                                                ? this.getGson().fromJson(
                                                        s.substring( s.indexOf( "transaction_id" ) - 2, s.indexOf( "sex" ) + 9 ),
                                                        Person.class
                                                )
                                                : new Person()
                                    )
                                )
                                : super.convert( new CrossBoardInfo().generate( SerialNumber, Errors.DATA_NOT_FOUND ) );
                    } )
                    .retryWhen(
                            Retry.backoff( 2, Duration.ofSeconds( 1 ) )
                                .doBeforeRetry( retrySignal -> super.logging( retrySignal, Methods.GET_CROSS_BOARDING ) )
                                .doAfterRetry( retrySignal -> super.logging( Methods.GET_CROSS_BOARDING, retrySignal ) )
                                .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() )
                    ).onErrorResume(
                            ConnectTimeoutException.class,
                            throwable -> super.convert(
                                    new CrossBoardInfo().generate(
                                            throwable.getMessage(),
                                            Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                    )
                            )
                    ).onErrorResume(
                            IllegalArgumentException.class,
                            throwable -> super.convert(
                                    new CrossBoardInfo().generate(
                                            Methods.GET_CROSS_BOARDING.name(),
                                            Errors.TOO_MANY_RETRIES_ERROR
                                    )
                            )
                    ).doOnError( e -> super.logging( e, Methods.GET_CROSS_BOARDING, SerialNumber ) )
                    .onErrorReturn(
                            new CrossBoardInfo().generate(
                                            Errors.SERVICE_WORK_ERROR.name(),
                                            Errors.SERVICE_WORK_ERROR
                            )
                    );

    private final Function< String, String > base64ToLink = base64 -> {
            super.getFields().clear();
            final HttpResponse< JsonNode > response;
            super.getFields().put( "photo", base64 );
            super.getFields().put( "serviceName", "psychologyCard" );

            try {
                super.logging( "Converting image to Link in: " + Methods.CONVERT_BASE64_TO_LINK );

                response = Unirest.post( super.getBASE64_IMAGE_TO_LINK_CONVERTER_API() )
                        .header("Content-Type", "application/json")
                        .body( "{\r\n    \"serviceName\" : \"psychologyCard\",\r\n    \"photo\" : \"" + base64 + "\"\r\n}" )
                        .asJson();

                return response.getStatus() == 200
                        ? response
                        .getBody()
                        .getObject()
                        .get( "data" )
                        .toString()
                        : Errors.DATA_NOT_FOUND.name();
            }
            catch ( final UnirestException e ) {
                super.logging( e, Methods.BASE64_TO_LINK, base64 );
                super.saveErrorLog(
                        Methods.CONVERT_BASE64_TO_LINK,
                        Methods.CONVERT_BASE64_TO_LINK.name(),
                        "Error: " + e.getMessage()
                );

                return Errors.SERVICE_WORK_ERROR.name();
            }
    };

    private final Function< String, Mono< Pinpp > > getPinpp = pinfl -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + super.getTokenForPassport() ) )
            .get()
            .uri( super.getAPI_FOR_PINPP() + pinfl )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.getUpdateTokens().get().getGetPinpp().apply( pinfl );
                case 500 | 501 | 502 | 503 -> ( Mono< Pinpp > ) super.saveErrorLog.apply( res.status().toString(), Methods.GET_PINPP );
                default -> super.checkResponse( res, content )
                        ? content
                        .asString()
                        .map( s -> this.getGson().fromJson( s, Pinpp.class ) )
                        : super.convert( new Pinpp().generate( pinfl, Errors.DATA_NOT_FOUND ) );
            } )
            .retryWhen(
                    Retry.backoff( 2, Duration.ofSeconds( 1 ) )
                        .doBeforeRetry( retrySignal -> super.logging( retrySignal, Methods.GET_PINPP ) )
                        .doAfterRetry( retrySignal -> super.logging( Methods.GET_PINPP, retrySignal ) )
                        .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() )
            ).onErrorResume(
                    io.netty.channel.ConnectTimeoutException.class,
                    throwable -> super.convert(
                            new Pinpp().generate(
                                    throwable.getMessage(),
                                    Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                            )
                    )
            ).onErrorResume(
                    IllegalArgumentException.class,
                    throwable -> super.convert(
                            new Pinpp().generate(
                                    Methods.GET_PINPP.name(),
                                    Errors.TOO_MANY_RETRIES_ERROR
                            )
                    )
            ).doOnError( throwable -> super.logging( throwable, Methods.GET_PINPP, pinfl ) )
            .doOnSuccess( value -> super.logging( Methods.GET_PINPP, value ) )
            .doOnSubscribe( value -> super.logging( super.getAPI_FOR_PINPP() ) );

    private final Function< String, Mono< Data > > getCadaster = cadaster -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + super.getTokenForPassport() ) )
            .post()
            .send(
                    ByteBufFlux.fromString(
                        CustomPublisherForRequest.generate(
                                cadaster,
                                new RequestForCadaster()
                        )
                    )
            ).uri( super.getAPI_FOR_CADASTR() )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.getUpdateTokens().get().getGetCadaster().apply( cadaster );
                case 501 | 502 | 503 -> ( Mono< Data > ) super.saveErrorLog.apply( res.status().toString(), Methods.CADASTER );
                default -> super.checkResponse( res, content )
                        ? content
                        .asString()
                        .map( s -> this.getGson().fromJson( s.substring( s.indexOf( "Data" ) + 6, s.indexOf( ",\"AnswereId" ) ), Data.class ) )
                        : super.convert( new Data().generate( cadaster, Errors.DATA_NOT_FOUND ) );
            } )
            .retryWhen(
                    Retry.backoff( 2, Duration.ofSeconds( 1 ) )
                        .doBeforeRetry( retrySignal -> super.logging( retrySignal, Methods.CADASTER ) )
                        .doAfterRetry( retrySignal -> super.logging( Methods.CADASTER, retrySignal ) )
                        .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() )
            ).onErrorResume(
                    io.netty.channel.ConnectTimeoutException.class,
                    throwable -> super.convert(
                            new Data().generate(
                                    throwable.getMessage(),
                                    Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                            )
                    )
            ).onErrorResume(
                    IllegalArgumentException.class,
                    throwable -> super.convert(
                            new Data().generate(
                                    Methods.CADASTER.name(),
                                    Errors.TOO_MANY_RETRIES_ERROR
                            )
                    )
            ).doOnError( e -> super.logging( e, Methods.CADASTER, cadaster ) )
            .doOnSuccess( value -> super.logging( Methods.CADASTER, value ) )
            .doOnSubscribe( value -> super.logging( super.getAPI_FOR_CADASTR() ) )
            .onErrorReturn(
                    new Data().generate(
                            Errors.SERVICE_WORK_ERROR.name(),
                            Errors.SERVICE_WORK_ERROR )
            );

    private final Function< String, Mono< String > > getImageByPinfl = pinfl -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + super.getTokenForGai() ) )
            .get()
            .uri( super.getAPI_FOR_PERSON_IMAGE() + pinfl )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.getUpdateTokens().get().getGetImageByPinfl().apply( pinfl );
                case 501 | 502 | 503 -> ( Mono< String > ) super.saveErrorLog.apply( res.status().toString(), Methods.GET_IMAGE_BY_PINFL );
                default -> super.checkResponse( res, content )
                        ? content
                        .asString()
                        .map( s -> s.substring( s.indexOf( "Data" ) + 7, s.indexOf( ",\"AnswereId" ) - 1 ) )
                        : super.convert( Errors.DATA_NOT_FOUND.name() );
            } )
            .retryWhen(
                    Retry.backoff( 2, Duration.ofSeconds( 1 ) )
                        .doBeforeRetry( retrySignal -> super.logging( retrySignal, Methods.GET_IMAGE_BY_PINFL ) )
                        .doAfterRetry( retrySignal -> super.logging( Methods.GET_IMAGE_BY_PINFL, retrySignal ) )
                        .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() )
            ).onErrorResume(
                    io.netty.channel.ConnectTimeoutException.class,
                    throwable -> super.convert( Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED + " : " + throwable.getMessage() )
            ).onErrorResume(
                    IllegalArgumentException.class,
                    throwable -> super.convert( Errors.TOO_MANY_RETRIES_ERROR + " : " + throwable.getMessage() )
            ).doOnError( e -> super.logging( e, Methods.GET_IMAGE_BY_PINFL, pinfl ) )
            .doOnSubscribe( value -> super.logging( super.getAPI_FOR_PERSON_IMAGE() ) )
            .onErrorReturn( Errors.DATA_NOT_FOUND.name() );

    private final Function< String, Mono< ModelForAddress > > getModelForAddress = pinfl -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + super.getTokenForGai() ) )
            .post()
            .uri( super.getAPI_FOR_MODEL_FOR_ADDRESS() )
            .send(
                    ByteBufFlux.fromString(
                            CustomPublisherForRequest.generate(
                                    pinfl,
                                    new RequestForModelOfAddress()
                            )
                    )
            ).responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.getUpdateTokens().get().getGetModelForAddress().apply( pinfl );
                case 501 | 502 | 503 -> ( Mono< ModelForAddress > ) super.saveErrorLog.apply(
                        res.status().toString(),
                        Methods.GET_MODEL_FOR_ADDRESS
                );
                default -> super.checkResponse( res, content )
                        ? content
                        .asString()
                        .map( s -> this.getGson().fromJson(
                                    s.substring( s.indexOf( "Data" ) + 6, s.indexOf( ",\"AnswereId" ) ),
                                    ModelForAddress.class
                            )
                        )
                        : super.convert( new ModelForAddress().generate( pinfl, Errors.DATA_NOT_FOUND ) );
            } )
            .retryWhen(
                    Retry.backoff( 2, Duration.ofSeconds( 1 ) )
                        .doBeforeRetry( retrySignal -> super.logging( retrySignal, Methods.GET_MODEL_FOR_ADDRESS ) )
                        .doAfterRetry( retrySignal -> super.logging( Methods.GET_MODEL_FOR_ADDRESS, retrySignal ) )
                        .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() )
            ).onErrorResume(
                    io.netty.channel.ConnectTimeoutException.class,
                    throwable -> super.convert(
                            new ModelForAddress().generate(
                                    throwable.getMessage(),
                                    Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                            )
                    )
            ).onErrorResume(
                    IllegalArgumentException.class,
                    throwable -> super.convert(
                            new ModelForAddress().generate(
                                    Methods.GET_MODEL_FOR_ADDRESS.name(),
                                    Errors.TOO_MANY_RETRIES_ERROR
                            )
                    )
            ).doOnError( e -> super.logging( e, Methods.GET_MODEL_FOR_ADDRESS, pinfl ) )
            .doOnSuccess( value -> super.logging( Methods.GET_MODEL_FOR_ADDRESS, value ) )
            .doOnSubscribe( value -> super.logging( super.getAPI_FOR_MODEL_FOR_ADDRESS() ) )
            .onErrorReturn(
                    new ModelForAddress().generate(
                            Errors.SERVICE_WORK_ERROR.name(),
                            Errors.SERVICE_WORK_ERROR
                    )
            );

    private final BiFunction< String, String, Mono< ModelForPassport > > getModelForPassport =
            ( SerialNumber, BirthDate ) -> this.getHttpClient()
                    .headers( h -> h.add( "Authorization", "Bearer " + super.getTokenForPassport() ) )
                    .post()
                    .uri( super.getAPI_FOR_PASSPORT_MODEL() )
                    .send(
                            ByteBufFlux.fromString(
                                    CustomPublisherForRequest.generate(
                                            String.join(
                                                    " ",
                                                    SerialNumber,
                                                    BirthDate
                                            ),
                                            new RequestForPassport()
                                    )
                            )
                    ).responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                        case 401 -> this.getUpdateTokens().get().getGetModelForPassport().apply( SerialNumber, BirthDate );
                        case 501 | 502 | 503 -> ( Mono< ModelForPassport > ) super.saveErrorLog.apply( res.status().toString(), Methods.GET_MODEL_FOR_PASSPORT );
                        default -> super.checkResponse( res, content )
                                ? content
                                .asString()
                                .map( s -> this.getGson().fromJson( s, ModelForPassport.class ) )
                                : super.convert(
                                        new ModelForPassport().generate(
                                                String.join(
                                                        " : ",
                                                        SerialNumber,
                                                        SerialNumber
                                                ),
                                                Errors.DATA_NOT_FOUND
                                        )
                                );
                    } )
                    .retryWhen(
                            Retry.backoff( 2, Duration.ofSeconds( 1 ) )
                                .doBeforeRetry( retrySignal -> super.logging( retrySignal, Methods.GET_MODEL_FOR_PASSPORT ) )
                                .doAfterRetry( retrySignal -> super.logging( Methods.GET_MODEL_FOR_PASSPORT, retrySignal ) )
                                .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() )
                    ).onErrorResume(
                            io.netty.channel.ConnectTimeoutException.class,
                            throwable -> super.convert(
                                    new ModelForPassport().generate(
                                            throwable.getMessage(),
                                            Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                                    )
                            )
                    ).onErrorResume(
                            IllegalArgumentException.class,
                            throwable -> super.convert(
                                    new ModelForPassport().generate(
                                            Methods.GET_MODEL_FOR_PASSPORT.name(),
                                            Errors.TOO_MANY_RETRIES_ERROR
                                    )
                            )
                    ).doOnError( e -> super.logging( e, Methods.GET_MODEL_FOR_PASSPORT, SerialNumber + "_" + BirthDate ) )
                    .doOnSuccess( value -> super.logging( Methods.GET_MODEL_FOR_PASSPORT, value ) )
                    .doOnSubscribe( value -> super.logging( super.getAPI_FOR_PASSPORT_MODEL() ) )
                    .onErrorReturn(
                            new ModelForPassport().generate(
                                    Errors.SERVICE_WORK_ERROR.name(),
                                    Errors.SERVICE_WORK_ERROR
                            )
                    );

    private final Function< String, Mono< Insurance > > insurance = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + super.getTokenForGai() ) )
            .get()
            .uri( super.getAPI_FOR_FOR_INSURANCE() + gosno )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.getUpdateTokens().get().getInsurance().apply( gosno );
                case 501 | 502 | 503 -> ( Mono< Insurance > ) super.saveErrorLog.apply( res.status().toString(), Methods.GET_INSURANCE );
                default -> super.checkResponse( res, content )
                        ? content
                        .asString()
                        .map( s -> !s.contains( "топилмади" )
                                ? this.getGson().fromJson( s, Insurance.class )
                                : new Insurance().generate( gosno, Errors.DATA_NOT_FOUND ) )
                        : super.convert( new Insurance().generate ( gosno, Errors.DATA_NOT_FOUND ) );
            } )
            .retryWhen(
                    Retry.backoff( 2, Duration.ofSeconds( 1 ) )
                        .doBeforeRetry( retrySignal -> super.logging( retrySignal, Methods.GET_INSURANCE ) )
                        .doAfterRetry( retrySignal -> super.logging( Methods.GET_INSURANCE, retrySignal ) )
                        .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() )
            ).onErrorResume(
                    io.netty.channel.ConnectTimeoutException.class,
                    throwable -> super.convert(
                            new Insurance().generate(
                                    throwable.getMessage(),
                                    Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                            )
                    )
            ).onErrorResume(
                    IllegalArgumentException.class,
                    throwable -> super.convert(
                            new Insurance().generate(
                                    Methods.GET_INSURANCE.name(),
                                    Errors.TOO_MANY_RETRIES_ERROR
                            )
                    )
            ).doOnError( e -> super.logging( e, Methods.GET_INSURANCE, gosno ) )
            .doOnSuccess( value -> super.logging( Methods.GET_INSURANCE, value ) )
            .doOnSubscribe( value -> super.logging( super.getAPI_FOR_FOR_INSURANCE() ) )
            .onErrorReturn(
                    new Insurance().generate(
                            Errors.SERVICE_WORK_ERROR.name(),
                            Errors.SERVICE_WORK_ERROR
                    )
            );

    private final Function< String, Mono< ModelForCar > > getVehicleData = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + super.getTokenForGai() ) )
            .get()
            .uri( super.getAPI_FOR_VEHICLE_DATA() + gosno )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.getUpdateTokens().get().getGetVehicleData().apply( gosno );
                case 501 | 502 | 503 -> ( Mono< ModelForCar > ) super.saveErrorLog.apply( res.status().toString(), Methods.GET_VEHILE_DATA );
                default -> super.checkResponse( res, content )
                        ? content
                        .asString()
                        .map( s -> this.getGson().fromJson( s, ModelForCar.class ) )
                        : super.convert( new ModelForCar().generate( gosno, Errors.DATA_NOT_FOUND ) );
            } )
            .retryWhen(
                    Retry.backoff( 2, Duration.ofSeconds( 1 ) )
                        .doBeforeRetry( retrySignal -> super.logging( retrySignal, Methods.GET_VEHILE_DATA ) )
                        .doAfterRetry( retrySignal -> super.logging( Methods.GET_VEHILE_DATA, retrySignal ) )
                        .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() )
            ).onErrorResume(
                    io.netty.channel.ConnectTimeoutException.class,
                    throwable -> super.convert(
                            new ModelForCar().generate(
                                    throwable.getMessage(),
                                    Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                            )
                    )
            ).onErrorResume(
                    IllegalArgumentException.class,
                    throwable -> super.convert(
                            new ModelForCar().generate(
                                    Methods.GET_VEHILE_DATA.name(),
                                    Errors.TOO_MANY_RETRIES_ERROR
                            )
                    )
            ).doOnError( e -> super.logging( e, Methods.GET_VEHILE_DATA, gosno ) )
            .doOnSuccess( value -> super.logging( Methods.GET_VEHILE_DATA, value ) )
            .doOnSubscribe( value -> super.logging( super.getAPI_FOR_VEHICLE_DATA() ) )
            .onErrorReturn(
                    new ModelForCar().generate(
                            Errors.SERVICE_WORK_ERROR.name(),
                            Errors.SERVICE_WORK_ERROR
                    )
            );

    private final Function< String, Mono< Tonirovka > > getVehicleTonirovka = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + super.getTokenForGai() ) )
            .get()
            .uri( super.getAPI_FOR_TONIROVKA() + gosno )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.getUpdateTokens().get().getGetVehicleTonirovka().apply( gosno );
                case 501 | 502 | 503 -> ( Mono< Tonirovka > ) super.saveErrorLog.apply( res.status().toString(), Methods.GET_TONIROVKA );
                default -> super.checkResponse( res, content )
                        ? content
                        .asString()
                        .map( s -> this.getGson().fromJson( s, Tonirovka.class ) )
                        : super.convert( new Tonirovka().generate( gosno, Errors.DATA_NOT_FOUND ) );
            } )
            .retryWhen(
                    Retry.backoff( 2, Duration.ofSeconds( 1 ) )
                        .doBeforeRetry( retrySignal -> super.logging( retrySignal, Methods.GET_TONIROVKA ) )
                        .doAfterRetry( retrySignal -> super.logging( Methods.GET_TONIROVKA, retrySignal ) )
                        .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() )
            ).onErrorResume(
                    io.netty.channel.ConnectTimeoutException.class,
                    throwable -> super.convert(
                            new Tonirovka().generate(
                                    throwable.getMessage(),
                                    Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                            )
                    )
            ).onErrorResume(
                    IllegalArgumentException.class,
                    throwable -> super.convert(
                            new Tonirovka().generate(
                                    Methods.GET_TONIROVKA.name(),
                                    Errors.TOO_MANY_RETRIES_ERROR
                            )
                    )
            ).doOnError( e -> super.logging( e, Methods.GET_TONIROVKA, gosno ) )
            .doOnSuccess( value -> super.logging( Methods.GET_TONIROVKA, value ) )
            .doOnSubscribe( value -> super.logging( super.getAPI_FOR_TONIROVKA() ) )
            .onErrorReturn(
                    new Tonirovka().generate(
                            Errors.SERVICE_WORK_ERROR.name(),
                            Errors.SERVICE_WORK_ERROR
                    )
            );

    private final Function< String, Mono< ViolationsList > > getViolationList = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + super.getTokenForGai() ) )
            .get()
            .uri( super.getAPI_FOR_VIOLATION_LIST() + gosno )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.getUpdateTokens().get().getGetViolationList().apply( gosno );
                case 501 | 502 | 503 -> ( Mono< ViolationsList > ) super.saveErrorLog.apply( res.status().toString(), Methods.GET_VIOLATION_LIST );
                default -> super.checkResponse( res, content )
                        ? content
                        .asString()
                        .map( s -> ViolationsList.generate( this.stringToArrayList( s, ViolationsInformation[].class ) ) )
                        : super.convert( new ViolationsList().generate( gosno, Errors.DATA_NOT_FOUND ) );
            } )
            .retryWhen(
                    Retry.backoff( 2, Duration.ofSeconds( 1 ) )
                        .doBeforeRetry( retrySignal -> super.logging( retrySignal, Methods.GET_VIOLATION_LIST ) )
                        .doAfterRetry( retrySignal -> super.logging( Methods.GET_VIOLATION_LIST, retrySignal ) )
                        .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() )
            ).onErrorResume(
                    io.netty.channel.ConnectTimeoutException.class,
                    throwable -> super.convert(
                            new ViolationsList().generate(
                                    throwable.getMessage(),
                                    Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                            )
                    )
            ).onErrorResume(
                    IllegalArgumentException.class,
                    throwable -> super.convert(
                            new ViolationsList().generate(
                                    Methods.GET_VIOLATION_LIST.name(),
                                    Errors.TOO_MANY_RETRIES_ERROR
                            )
                    )
            ).doOnError( e -> super.logging( e, Methods.GET_VIOLATION_LIST, gosno ) )
            .doOnSuccess( value -> super.logging( Methods.GET_VIOLATION_LIST, value ) )
            .doOnSubscribe( value -> super.logging( super.getAPI_FOR_VIOLATION_LIST() ) )
            .onErrorReturn(
                    new ViolationsList().generate(
                            Errors.SERVICE_WORK_ERROR.name(),
                            Errors.SERVICE_WORK_ERROR
                    )
            );

    private final Function< String, Mono< DoverennostList > > getDoverennostList = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + super.getTokenForGai() ) )
            .get()
            .uri( super.getAPI_FOR_DOVERENNOST_LIST() + gosno )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.getUpdateTokens().get().getGetDoverennostList().apply( gosno );
                case 501 | 502 | 503 -> ( Mono< DoverennostList > ) super.saveErrorLog.apply( res.status().toString(), Methods.GET_DOVERENNOST_LIST );
                default -> super.checkResponse( res, content )
                        ? content
                        .asString()
                        .map( s -> DoverennostList.generate( this.stringToArrayList( s, Doverennost[].class ) ) )
                        : super.convert( new DoverennostList().generate( gosno, Errors.DATA_NOT_FOUND ) );
            } )
            .retryWhen(
                    Retry.backoff( 2, Duration.ofSeconds( 1 ) )
                        .doBeforeRetry( retrySignal -> super.logging( retrySignal, Methods.GET_DOVERENNOST_LIST ) )
                        .doAfterRetry( retrySignal -> super.logging( Methods.GET_DOVERENNOST_LIST, retrySignal ) )
                        .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() )
            ).onErrorResume(
                    io.netty.channel.ConnectTimeoutException.class,
                    throwable -> super.convert(
                            new DoverennostList().generate(
                                    throwable.getMessage(),
                                    Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                            )
                    )
            ).onErrorResume(
                    IllegalArgumentException.class,
                    throwable -> super.convert(
                            new DoverennostList().generate(
                                    Methods.GET_DOVERENNOST_LIST.name(),
                                    Errors.TOO_MANY_RETRIES_ERROR
                            )
                    )
            ).doOnError( e -> super.logging( e, Methods.GET_DOVERENNOST_LIST, gosno ) )
            .doOnSuccess( value -> super.logging( Methods.GET_DOVERENNOST_LIST, value ) )
            .doOnSubscribe( value -> super.logging( super.getAPI_FOR_DOVERENNOST_LIST() ) )
            .onErrorReturn(
                    new DoverennostList().generate(
                            gosno,
                            Errors.SERVICE_WORK_ERROR
                    )
            );

    private final Function< String, Mono< ModelForCarList > > getModelForCarList = pinfl -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + super.getTokenForGai() ) )
            .get()
            .uri( super.getAPI_FOR_MODEL_FOR_CAR_LIST() + pinfl )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.getUpdateTokens().get().getGetModelForCarList().apply( pinfl );
                case 501 | 502 | 503 -> ( Mono< ModelForCarList > ) super.saveErrorLog.apply( res.status().toString(), Methods.GET_MODEL_FOR_CAR_LIST );
                default -> super.checkResponse( res, content )
                        ? content
                        .asString()
                        .map( s -> ModelForCarList.generate( this.stringToArrayList( s, ModelForCar[].class ) ) )
                        : super.convert( new ModelForCarList().generate( pinfl, Errors.DATA_NOT_FOUND ) );
            } )
            .retryWhen(
                    Retry.backoff( 2, Duration.ofSeconds( 2 ) )
                        .doBeforeRetry( retrySignal -> super.logging( retrySignal, Methods.GET_MODEL_FOR_CAR_LIST ) )
                        .doAfterRetry( retrySignal -> super.logging( Methods.GET_MODEL_FOR_CAR_LIST, retrySignal ) )
                        .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() )
            ).onErrorResume(
                    io.netty.channel.ConnectTimeoutException.class,
                    throwable -> super.convert(
                            new ModelForCarList().generate(
                                    throwable.getMessage(),
                                    Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED
                            )
                    )
            ).onErrorResume(
                    IllegalArgumentException.class,
                    throwable -> super.convert(
                            new ModelForCarList().generate(
                                    Methods.GET_MODEL_FOR_CAR_LIST.name(),
                                    Errors.TOO_MANY_RETRIES_ERROR
                            )
                    )
            ).doOnError( e -> super.logging( e, Methods.GET_MODEL_FOR_CAR_LIST, pinfl ) )
            .doOnSuccess( value -> super.logging( Methods.GET_MODEL_FOR_CAR_LIST, value ) )
            .doOnSubscribe( value -> super.logging( super.getAPI_FOR_MODEL_FOR_CAR_LIST() ) )
            .onErrorReturn(
                    new ModelForCarList().generate(
                            Errors.SERVICE_WORK_ERROR.name(),
                            Errors.SERVICE_WORK_ERROR
                    )
            );

    private final Function< ModelForCarList, Mono< ModelForCarList > > findAllAboutCarList = modelForCarList ->
            Flux.fromStream( modelForCarList.getModelForCarList().stream() )
                    .parallel( super.checkDifference( modelForCarList.getModelForCarList().size() ) )
                    .runOn( Schedulers.parallel() )
                    .flatMap( modelForCar -> Mono.zip(
                                this.getInsurance().apply( modelForCar.getPlateNumber() ),
                                this.getGetVehicleTonirovka().apply( modelForCar.getPlateNumber() ),
                                this.getGetDoverennostList().apply( modelForCar.getPlateNumber() )
                            ).map( tuple3 -> modelForCar.save( tuple3, modelForCarList ) )
                    ).sequential()
                    .publishOn( Schedulers.single() )
                    .take( 1 )
                    .single();

    private final Function< PsychologyCard, Mono< PsychologyCard > > findAllDataAboutCar = psychologyCard ->
            super.check( psychologyCard )
                    ? Flux.fromStream(
                            psychologyCard
                                .getModelForCarList()
                                .getModelForCarList()
                                .stream()
                    ).parallel(
                            super.checkDifference(
                                psychologyCard
                                        .getModelForCarList()
                                        .getModelForCarList()
                                        .size()
                            )
                    ).runOn( Schedulers.parallel() )
                    .flatMap( modelForCar -> Mono.zip(
                                this.getInsurance().apply( modelForCar.getPlateNumber() ),
                                this.getGetVehicleTonirovka().apply( modelForCar.getPlateNumber() ),
                                this.getGetDoverennostList().apply( modelForCar.getPlateNumber() )
                            ).map( tuple3 -> modelForCar.save( tuple3, psychologyCard ) )
                    )
                    .sequential()
                    .publishOn( Schedulers.single() )
                    .take( 1 )
                    .single()
                    : super.convert( psychologyCard );

    private final Function< PsychologyCard, Mono< PsychologyCard > > setPersonPrivateDataAsync = psychologyCard ->
            super.checkPinpp( psychologyCard )
                    ? this.getGetCadaster().apply( psychologyCard.getPinpp().getCadastre() )
                    .flatMap( data -> super.checkCadastor( psychologyCard.save( data ) )
                            ? Flux.fromStream(
                                    psychologyCard
                                    .getModelForCadastr()
                                    .getPermanentRegistration()
                                    .stream()
                            ).parallel(
                                    super.checkDifference(
                                        psychologyCard
                                                .getModelForCadastr()
                                                .getPermanentRegistration()
                                                .size()
                                    )
                            ).runOn( Schedulers.parallel() )
                            .filter( person -> super.checkPerson( person, psychologyCard.getPinpp() ) )
                            .sequential()
                            .publishOn( Schedulers.single() )
                            .take( 1 )
                            .single()
                            .flatMap( person -> Mono.zip(
                                        this.getGetModelForAddress().apply( person.getPCitizen() ),
                                        this.getGetModelForPassport().apply( person.getPPsp(), person.getPDateBirth() )
                                    )
                            ).map( psychologyCard::save )
                            .onErrorResume( throwable -> super.convert(
                                    new PsychologyCard().generate(
                                            throwable.getMessage(),
                                            Errors.SERVICE_WORK_ERROR
                                    )
                                )
                            )
                            : super.convert( psychologyCard )
                    )
                    : super.convert( psychologyCard );

    public Mono< PsychologyCard > getPsychologyCard (
            final String token,
            final PsychologyCard psychologyCard,
            final ApiResponseModel apiResponseModel
    ) {
        try {
            super.getHeaders().put( "Authorization", "Bearer " + token );

            psychologyCard.setForeignerList(
                    this.stringToArrayList(
                            Unirest.get(
                                    String.join(
                                            "",
                                            super.getAPI_FOR_TRAIN_TICKET_CONSUMER_SERVICE(),
                                            psychologyCard.getPapilonData().get( 0 ).getPassport()
                                    )
                            ).headers( super.getHeaders() )
                            .asJson()
                            .getBody()
                            .getObject()
                            .get( "data" )
                            .toString(),
                            Foreigner[].class
                    )
            );

            super.saveUserUsageLog.apply( psychologyCard, apiResponseModel );
        } catch ( final Exception e ) {
            super.saveErrorLog(
                    Methods.GET_PSYCHOLOGY_CARD,
                    psychologyCard.getPapilonData().get( 0 ).getPassport(),
                    Errors.DATA_NOT_FOUND.name()
            );
        }
        return super.convert( psychologyCard );
    }

    private final Function< FIO, Mono< PersonTotalDataByFIO > > getPersonTotalDataByFIO = fio -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + super.getTokenForFio() ) )
            .post()
            .uri( super.getAPI_FOR_PERSON_DATA_FROM_ZAKS() )
            .send(
                    ByteBufFlux.fromString(
                            CustomPublisherForRequest.generate(
                                    fio,
                                    new RequestForFio()
                            )
                    )
            ).responseSingle( ( res, content ) -> res.status().code() == 401
                    ? this.getUpdateTokens().get().getGetPersonTotalDataByFIO().apply( fio )
                    : super.checkResponse( res, content )
                    ? content
                    .asString()
                    .map( s -> {
                        final PersonTotalDataByFIO person = this.getGson().fromJson( s, PersonTotalDataByFIO.class );

                        if ( super.objectIsNotNull( person ) && super.isCollectionNotEmpty( person.getData() ) ) {
                            super.analyze(
                                    person.getData(),
                                    person1 -> this.getGetImageByPinfl()
                                            .apply( person1.getPinpp() )
                                            .subscribe( person1::setPersonImage )
                            );
                        }

                        return super.objectIsNotNull( person ) ? person : PersonTotalDataByFIO.generate();
                    } )
                    : super.convert(
                            PersonTotalDataByFIO.generate().generate(
                                    fio.getName(),
                                    Errors.DATA_NOT_FOUND
                            )
                    )
            )
            .doOnError( e -> super.logging( e, Methods.GET_DATA_BY_FIO, fio.getName() ) )
            .onErrorReturn(
                    PersonTotalDataByFIO.generate().generate(
                            Errors.SERVICE_WORK_ERROR.name(),
                            Errors.SERVICE_WORK_ERROR
                    )
            );

    private final Function< ApiResponseModel, Mono< PsychologyCard > > getPsychologyCardByPinfl =
            apiResponseModel -> this.checkParam( apiResponseModel.getStatus().getMessage() )
                    ? Mono.zip(
                            this.getGetPinpp().apply( apiResponseModel.getStatus().getMessage() ),
                            this.getGetImageByPinfl().apply( apiResponseModel.getStatus().getMessage() ),
                            this.getGetModelForCarList().apply( apiResponseModel.getStatus().getMessage() ),
                            FindFaceComponent
                                    .getInstance()
                                    .getViolationListByPinfl
                                    .apply( apiResponseModel.getStatus().getMessage() )
                                    .onErrorReturn( super.emptyList() )
                    ).map( PsychologyCard::generate )
                    .flatMap( psychologyCard -> Mono.zip(
                                    this.getFindAllDataAboutCar().apply( psychologyCard ),
                                    this.getSetPersonPrivateDataAsync().apply( psychologyCard )
                            ).map( tuple1 -> super.saveUserUsageLog.apply( psychologyCard, apiResponseModel ) )
                    )
                    : super.convert(
                            new PsychologyCard().generate(
                                            Errors.WRONG_PARAMS.name(),
                                            Errors.SERVICE_WORK_ERROR
                            )
                    );

    private final Function< ApiResponseModel, Mono< PsychologyCard > > getPsychologyCardByPinflInitial =
            apiResponseModel -> this.checkParam( apiResponseModel.getStatus().getMessage() )
                    ? Mono.zip(
                            this.getGetPinpp().apply( apiResponseModel.getStatus().getMessage() ),
                            this.getGetImageByPinfl().apply( apiResponseModel.getStatus().getMessage() )
                    ).map( tuple -> super.saveUserUsageLog.apply( PsychologyCard.generate( tuple ), apiResponseModel ) )
                    : super.convert(
                            new PsychologyCard().generate(
                                            Errors.WRONG_PARAMS.name(),
                                            Errors.SERVICE_WORK_ERROR
                            )
                    );

    private final BiFunction< Results, ApiResponseModel, Mono< PsychologyCard > > getPsychologyCardByImage =
            ( results, apiResponseModel ) -> Mono.zip(
                            this.getGetPinpp().apply( results.getResults().get( 0 ).getPersonal_code() ),
                            this.getGetImageByPinfl().apply( results.getResults().get( 0 ).getPersonal_code() ),
                            this.getGetModelForCarList().apply( results.getResults().get( 0 ).getPersonal_code() )
                    ).map( tuple -> PsychologyCard.generate( results, tuple ) )
                    .flatMap( psychologyCard -> Mono.zip(
                                    this.getFindAllDataAboutCar().apply( psychologyCard ),
                                    this.getSetPersonPrivateDataAsync().apply( psychologyCard )
                            ).map( tuple1 -> super.saveUserUsageLog.apply( psychologyCard, apiResponseModel ) )
                    );

    private final BiFunction< ModelForPassport, ApiResponseModel, Mono< PsychologyCard > >
            getPsychologyCardByData = ( data, apiResponseModel ) -> this.check( data )
                    ? Mono.zip(
                            this.getGetPinpp().apply( data.getData().getPerson().getPinpp() ),
                            this.getGetImageByPinfl().apply( data.getData().getPerson().getPinpp() ),
                            this.getGetModelForCarList().apply( data.getData().getPerson().getPinpp() ),
                            this.getGetModelForAddress().apply( data.getData().getPerson().getPCitizen() ),
                            FindFaceComponent
                                    .getInstance()
                                    .getViolationListByPinfl
                                    .apply( data.getData().getPerson().getPinpp() )
                                    .onErrorReturn( super.emptyList() )
                    ).flatMap(
                            tuple -> this.getFindAllDataAboutCar()
                                    .apply( PsychologyCard.generate( data, tuple ) )
                                    .map( psychologyCard -> super.saveUserUsageLog.apply( psychologyCard, apiResponseModel ) )
                    )
                    : super.convert(
                            new PsychologyCard().generate(
                                    data.getData().getPerson().getPinpp(),
                                    Errors.DATA_NOT_FOUND
                            )
                    );

    private final Function< CrossBoardInfo, Mono< CrossBoardInfo > > analyzeCrossData = crossBoardInfo ->
            Flux.fromStream(
                        crossBoardInfo
                                .getData()
                                .get( 0 )
                                .getCrossBoardList()
                                .stream()
                    ).parallel(
                            super.checkDifference(
                                    crossBoardInfo
                                            .getData()
                                            .get( 0 )
                                            .getCrossBoardList()
                                            .size()
                            )
                    ).runOn( Schedulers.parallel() )
                    .map( crossBoard -> crossBoard.save( crossBoardInfo.getData().get( 0 ).getPerson().getNationalityid() ) )
                    .sequential()
                    .publishOn( Schedulers.single() )
                    .collectList()
                    .map( crossBoards -> {
                        crossBoardInfo
                                .getData()
                                .get( 0 )
                                .getCrossBoardList()
                                .sort( Comparator.comparing( CrossBoard::getRegistrationDate ).reversed() );

                        return crossBoardInfo;
                    } );

    @Override
    public void close() {
        serDes = null;
        this.setFlag( false );
        this.getThread().interrupt();
        this.semaphore.release();
        SerDes.getSerDes();
    }

    @Override
    public void close(
        final Throwable throwable
    ) {
        super.logging( throwable, Methods.UPDATE_TOKENS, throwable.getMessage() );
        this.close();
    }
}