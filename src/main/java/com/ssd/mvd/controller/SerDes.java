package com.ssd.mvd.controller;

import java.util.*;
import java.time.Duration;
import java.util.function.*;
import java.util.concurrent.TimeUnit;


import reactor.util.retry.Retry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import reactor.netty.ByteBufFlux;
import io.netty.handler.logging.LogLevel;
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
import com.ssd.mvd.kafka.KafkaDataControl;
import com.ssd.mvd.entity.foreigner.Foreigner;
import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.entity.modelForCadastr.Data;
import com.ssd.mvd.entityForLogging.UserRequest;
import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.modelForFioOfPerson.PersonTotalDataByFIO;

@lombok.Data
public class SerDes implements Runnable {
    private Boolean flag = true;
    private String tokenForGai;
    private String tokenForFio;
    private String tokenForPassport;

    private final Gson gson = new Gson();
    private final Config config = new Config();
    private static SerDes serDes = new SerDes();
    private final HttpClient httpClient = HttpClient
            .create()
            .responseTimeout( Duration.ofMinutes( 1 ) )
            .headers( h -> h.add( "Content-Type", "application/json" ) )
            .wiretap( "reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL );

    private final Map< String, Object > fields = new HashMap<>();
    private final Map< String, String > headers = new HashMap<>();

    public static SerDes getSerDes () { return serDes != null ? serDes : ( serDes = new SerDes() ); }

    private <T> List<T> stringToArrayList ( String object, Class< T[] > clazz ) { return Arrays.asList( this.getGson().fromJson( object, clazz ) ); }

    private SerDes () {
        Unirest.setObjectMapper( new ObjectMapper() {
            private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

            @Override
            public String writeValue( Object o ) {
                try { return this.objectMapper.writeValueAsString( o ); }
                catch ( JsonProcessingException e ) { throw new RuntimeException(e); } }

            @Override
            public <T> T readValue( String s, Class<T> aClass ) {
                try { return this.objectMapper.readValue( s, aClass ); }
                catch ( JsonProcessingException e ) { throw new RuntimeException(e); } } } );
        this.getHeaders().put( "accept", "application/json" ); }

    private SerDes updateTokens () {
        LogInspector
                .getInstance()
                .getLOGGER()
                .info( "Updating tokens..." );
        this.getFields().put( "Login", this.getConfig().getLOGIN_FOR_GAI_TOKEN() );
        this.getFields().put( "Password" , this.getConfig().getPASSWORD_FOR_GAI_TOKEN() );
        this.getFields().put( "CurrentSystem", this.getConfig().getCURRENT_SYSTEM_FOR_GAI() );
        try { this.setTokenForGai( String.valueOf( Unirest.post( this.getConfig().getAPI_FOR_GAI_TOKEN() )
                .fields( this.getFields() )
                .asJson()
                .getBody()
                .getObject()
                .get( "access_token" ) ) );
            this.setTokenForPassport( this.getTokenForGai() );
            this.setFlag( true );
            return this; }
        catch ( UnirestException e ) {
            this.setFlag( false );
            ErrorController
                    .getInstance()
                    .saveErrorLog( e.getMessage() );
            ErrorController
                    .getInstance()
                    .saveErrorLog(
                            Methods.UPDATE_TOKENS.name(),
                            "access_token",
                            "Error: " + e.getMessage() );
            this.updateTokens(); }
        return this; }

    // сохраняем логи о пользователе который отправил запрос на сервис
    private final BiFunction< PsychologyCard, ApiResponseModel, PsychologyCard > saveUserUsageLog =
            ( psychologyCard, apiResponseModel ) -> {
                KafkaDataControl
                        .getInstance()
                        .getWriteToKafkaServiceUsage()
                        .accept( this.getGson().toJson( new UserRequest( psychologyCard, apiResponseModel ) ) );
                return psychologyCard; };

    private final Function< String, String > base64ToLink = base64 -> {
        this.getFields().clear();
        HttpResponse< JsonNode > response;
        this.getFields().put( "photo", base64 );
        this.getFields().put( "serviceName", "psychologyCard" );
        try { LogInspector
                .getInstance()
                .getLOGGER()
                .info( "Converting image to Link in: " + Methods.CONVERT_BASE64_TO_LINK );
            response = Unirest.post( this.getConfig().getBASE64_IMAGE_TO_LINK_CONVERTER_API() )
                    .header("Content-Type", "application/json")
                    .body( "{\r\n    \"serviceName\" : \"psychologyCard\",\r\n    \"photo\" : \"" + base64 + "\"\r\n}" )
                    .asJson();
            return response.getStatus() == 200
                    ? response
                    .getBody()
                    .getObject()
                    .get( "data" )
                    .toString()
                    : Errors.DATA_NOT_FOUND.name(); }
        catch ( UnirestException e ) {
            ErrorController
                    .getInstance()
                    .saveErrorLog(
                            this.getConfig().getBASE64_IMAGE_TO_LINK_CONVERTER_API(),
                            Methods.CONVERT_BASE64_TO_LINK.name(),
                            "Error: " + e.getMessage() );
            return Errors.SERVICE_WORK_ERROR.name(); } };

    private final Function< String, Mono< Pinpp > > getPinpp = pinfl -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForPassport() ) )
            .get()
            .uri( this.getConfig().getAPI_FOR_PINPP() + pinfl )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.updateTokens().getGetPinpp().apply( pinfl );
                case 500 | 501 | 502 | 503 -> ( Mono< Pinpp > ) ErrorController
                        .getInstance()
                        .getSaveErrorLog()
                        .apply( res.status().toString(), Methods.GET_PINPP );
                default -> DataValidationInspector
                        .getInstance()
                        .getCheckResponse()
                        .apply( res, content )
                        ? content
                        .asString()
                        .map( s -> this.getGson().fromJson( s, Pinpp.class ) )
                        : Mono.just( new Pinpp( ErrorController
                        .getInstance()
                        .getGetDataNotFoundErrorResponse().apply( pinfl ) ) ); } )
            .retryWhen( Retry.backoff( 2, Duration.ofSeconds( 2 ) )
                    .doBeforeRetry( retrySignal -> LogInspector
                            .getInstance()
                            .logging( retrySignal, Methods.GET_PINPP ) )
                    .doAfterRetry( retrySignal -> LogInspector
                            .getInstance()
                            .logging( Methods.GET_PINPP, retrySignal ) )
                    .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() ) )
            .onErrorResume( io.netty.channel.ConnectTimeoutException.class,
                    throwable -> Mono.just( new Pinpp( ErrorController
                            .getInstance()
                            .getGetConnectionError().apply( throwable.getMessage() ) ) ) )
            .onErrorResume( IllegalArgumentException.class,
                    throwable -> Mono.just( new Pinpp( ErrorController
                            .getInstance()
                            .getGetTooManyRetriesError().apply( Methods.GET_PINPP ) ) ) )
            .doOnError( throwable -> LogInspector
                    .getInstance()
                    .logging( throwable, Methods.GET_PINPP, pinfl ) )
            .doOnSuccess( value -> LogInspector
                    .getInstance()
                    .logging( Methods.GET_PINPP, value ) )
            .doOnSubscribe( value -> LogInspector
                    .getInstance()
                    .logging( this.getConfig().getAPI_FOR_PINPP() ) );

    private final Function< String, Mono< Data > > getCadaster = cadaster -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForPassport() ) )
            .post()
            .send( ByteBufFlux.fromString( Mono.just(
                    this.getGson().toJson( new RequestForCadaster( cadaster ) ) ) ) )
            .uri( this.getConfig().getAPI_FOR_CADASTR() )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.updateTokens().getGetCadaster().apply( cadaster );
                case 501 | 502 | 503 -> ( Mono< Data > ) ErrorController
                        .getInstance()
                        .getSaveErrorLog()
                        .apply( res.status().toString(), Methods.CADASTER );
                default -> DataValidationInspector
                        .getInstance()
                        .getCheckResponse()
                        .apply( res, content )
                        ? content
                        .asString()
                        .map( s -> this.getGson().fromJson(
                                s.substring( s.indexOf( "Data" ) + 6, s.indexOf( ",\"AnswereId" ) ), Data.class ) )
                        : Mono.just( new Data( ErrorController
                        .getInstance()
                        .getGetDataNotFoundErrorResponse().apply( cadaster ) ) ); } )
            .retryWhen( Retry.backoff( 2, Duration.ofSeconds( 2 ) )
                    .doBeforeRetry( retrySignal -> LogInspector
                            .getInstance()
                            .logging( retrySignal, Methods.CADASTER ) )
                    .doAfterRetry( retrySignal -> LogInspector
                            .getInstance()
                            .logging( Methods.CADASTER, retrySignal ) )
                    .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() ) )
            .onErrorResume( io.netty.channel.ConnectTimeoutException.class,
                    throwable -> Mono.just( new Data(
                            ErrorController
                                        .getInstance()
                                    .getGetConnectionError().apply( throwable.getMessage() ) ) ) )
            .onErrorResume( IllegalArgumentException.class,
                    throwable -> Mono.just( new Data(
                            ErrorController
                                    .getInstance()
                                    .getGetTooManyRetriesError().apply( Methods.CADASTER ) ) ) )
            .doOnError( e -> LogInspector
                    .getInstance()
                    .logging( e, Methods.CADASTER, cadaster ) )
            .doOnSuccess( value -> LogInspector
                    .getInstance()
                    .logging( Methods.CADASTER, value ) )
            .doOnSubscribe( value -> LogInspector
                    .getInstance()
                    .logging( this.getConfig().getAPI_FOR_CADASTR() ) )
            .onErrorReturn( new Data( ErrorController
                    .getInstance()
                    .getGetServiceErrorResponse()
                    .apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< String > > getImageByPinfl = pinfl -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForGai() ) )
            .get()
            .uri( this.getConfig().getAPI_FOR_PERSON_IMAGE() + pinfl )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.updateTokens().getGetImageByPinfl().apply( pinfl );
                case 501 | 502 | 503 -> ( Mono< String > ) ErrorController
                        .getInstance()
                        .getSaveErrorLog()
                        .apply( res.status().toString(), Methods.GET_IMAGE_BY_PINFL );
                default -> DataValidationInspector
                        .getInstance()
                        .getCheckResponse()
                        .apply( res, content )
                        ? content
                        .asString()
                        .map( s -> s.substring( s.indexOf( "Data" ) + 7, s.indexOf( ",\"AnswereId" ) - 1 ) )
                        : Mono.just( Errors.DATA_NOT_FOUND.name() ); } )
            .retryWhen( Retry.backoff( 2, Duration.ofSeconds( 2 ) )
                    .doBeforeRetry( retrySignal -> LogInspector
                            .getInstance()
                            .logging( retrySignal, Methods.GET_IMAGE_BY_PINFL ) )
                    .doAfterRetry( retrySignal -> LogInspector
                            .getInstance()
                            .logging( Methods.GET_IMAGE_BY_PINFL, retrySignal ) )
                    .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() ) )
            .onErrorResume( io.netty.channel.ConnectTimeoutException.class,
                    throwable -> Mono.just(
                            Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED + " : " + throwable.getMessage() ) )
            .onErrorResume( IllegalArgumentException.class,
                    throwable -> Mono.just( Errors.TOO_MANY_RETRIES_ERROR + " : " + throwable.getMessage() ) )
            .doOnError( e -> LogInspector
                    .getInstance()
                    .logging( e, Methods.GET_IMAGE_BY_PINFL, pinfl ) )
            .doOnSubscribe( value -> LogInspector
                    .getInstance()
                    .logging( this.getConfig().getAPI_FOR_PERSON_IMAGE() ) )
            .onErrorReturn( Errors.DATA_NOT_FOUND.name() );

    private final Function< String, Mono< ModelForAddress > > getModelForAddress = pinfl -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForGai() ) )
            .post()
            .uri( this.getConfig().getAPI_FOR_MODEL_FOR_ADDRESS() )
            .send( ByteBufFlux.fromString( Mono.just( this.getGson().toJson( new RequestForModelOfAddress( pinfl ) ) ) ) )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.updateTokens().getGetModelForAddress().apply( pinfl );
                case 501 | 502 | 503 -> ( Mono< ModelForAddress > ) ErrorController
                        .getInstance()
                        .getSaveErrorLog()
                        .apply( res.status().toString(), Methods.GET_MODEL_FOR_ADDRESS );
                default -> DataValidationInspector
                        .getInstance()
                        .getCheckResponse()
                        .apply( res, content )
                        ? content
                        .asString()
                        .map( s -> this.getGson().fromJson(
                                s.substring( s.indexOf( "Data" ) + 6, s.indexOf( ",\"AnswereId" ) ),
                                ModelForAddress.class ) )
                        : Mono.just( new ModelForAddress( ErrorController
                        .getInstance()
                        .getGetDataNotFoundErrorResponse().apply( pinfl ) ) ); } )
            .retryWhen( Retry.backoff( 2, Duration.ofSeconds( 2 ) )
                    .doBeforeRetry( retrySignal -> LogInspector
                            .getInstance()
                            .logging( retrySignal, Methods.GET_MODEL_FOR_ADDRESS ) )
                    .doAfterRetry( retrySignal -> LogInspector
                            .getInstance()
                            .logging( Methods.GET_MODEL_FOR_ADDRESS, retrySignal ) )
                    .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() ) )
            .onErrorResume( io.netty.channel.ConnectTimeoutException.class,
                    throwable -> Mono.just( new ModelForAddress(
                            ErrorController
                                    .getInstance()
                                    .getGetConnectionError()
                                    .apply( throwable.getMessage() ) ) ) )
            .onErrorResume( IllegalArgumentException.class,
                    throwable -> Mono.just( new ModelForAddress(
                            ErrorController
                                    .getInstance()
                                    .getGetTooManyRetriesError()
                                    .apply( Methods.GET_MODEL_FOR_ADDRESS ) ) ) )
            .doOnError( e -> LogInspector
                    .getInstance()
                    .logging( e, Methods.GET_MODEL_FOR_ADDRESS, pinfl ) )
            .doOnSuccess( value -> LogInspector
                    .getInstance()
                    .logging( Methods.GET_MODEL_FOR_ADDRESS, value ) )
            .doOnSubscribe( value -> LogInspector
                    .getInstance()
                    .logging( this.getConfig().getAPI_FOR_MODEL_FOR_ADDRESS() ) )
            .onErrorReturn( new ModelForAddress( ErrorController
                    .getInstance()
                    .getGetServiceErrorResponse()
                    .apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final BiFunction< String, String, Mono< com.ssd.mvd.entity.modelForPassport.ModelForPassport > > getModelForPassport =
            ( SerialNumber, BirthDate ) -> this.getHttpClient()
                    .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForPassport() ) )
                    .post()
                    .uri( this.getConfig().getAPI_FOR_PASSPORT_MODEL() )
                    .send( ByteBufFlux.fromString( Mono.just( this.getGson().toJson(
                            new RequestForPassport( SerialNumber, BirthDate ) ) ) ) )
                    .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                        case 401 -> this.updateTokens().getGetModelForPassport().apply( SerialNumber, BirthDate );
                        case 501 | 502 | 503 -> ( Mono< com.ssd.mvd.entity.modelForPassport.ModelForPassport > )
                                ErrorController
                                        .getInstance()
                                        .getSaveErrorLog().apply( res.status().toString(), Methods.GET_MODEL_FOR_PASSPORT );
                        default -> res.status().code() == 200
                                && content != null
                                ? content
                                .asString()
                                .map( s -> this.getGson()
                                        .fromJson( s, com.ssd.mvd.entity.modelForPassport.ModelForPassport.class ) )
                                : Mono.just( new com.ssd.mvd.entity.modelForPassport.ModelForPassport(
                                ErrorController
                                        .getInstance()
                                        .getGetDataNotFoundErrorResponse()
                                        .apply( SerialNumber + " : " + SerialNumber ) ) ); } )
                    .retryWhen( Retry.backoff( 2, Duration.ofSeconds( 2 ) )
                            .doBeforeRetry( retrySignal -> LogInspector
                                    .getInstance()
                                    .logging( retrySignal, Methods.GET_MODEL_FOR_PASSPORT ) )
                            .doAfterRetry( retrySignal -> LogInspector
                                    .getInstance()
                                    .logging( Methods.GET_MODEL_FOR_PASSPORT, retrySignal ) )
                            .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() ) )
                    .onErrorResume( io.netty.channel.ConnectTimeoutException.class,
                            throwable -> Mono.just( new com.ssd.mvd.entity.modelForPassport.ModelForPassport(
                                    ErrorController
                                        .getInstance()
                                            .getGetConnectionError().apply( throwable.getMessage() ) ) ) )
                    .onErrorResume( IllegalArgumentException.class,
                            throwable -> Mono.just( new com.ssd.mvd.entity.modelForPassport.ModelForPassport(
                                    ErrorController
                                            .getInstance()
                                            .getGetTooManyRetriesError()
                                            .apply( Methods.GET_MODEL_FOR_PASSPORT ) ) ) )
                    .doOnError( e -> LogInspector
                    .getInstance()
                    .logging( e, Methods.GET_MODEL_FOR_PASSPORT, SerialNumber + "_" + BirthDate ) )
                    .doOnSuccess( value -> LogInspector
                    .getInstance()
                    .logging( Methods.GET_MODEL_FOR_PASSPORT, value ) )
                    .doOnSubscribe( value -> LogInspector
                    .getInstance()
                    .logging( this.getConfig().getAPI_FOR_PASSPORT_MODEL() ) )
                    .onErrorReturn( new com.ssd.mvd.entity.modelForPassport.ModelForPassport(
                            ErrorController
                                    .getInstance()
                                    .getGetServiceErrorResponse()
                                    .apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< Insurance > > insurance = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForGai() ) )
            .get()
            .uri( this.getConfig().getAPI_FOR_FOR_INSURANCE() + gosno )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.updateTokens().getInsurance().apply( gosno );
                case 501 | 502 | 503 -> ( Mono< Insurance > ) ErrorController
                        .getInstance()
                        .getSaveErrorLog()
                        .apply( res.status().toString(), Methods.GET_INSURANCE );
                default -> DataValidationInspector
                        .getInstance()
                        .getCheckResponse()
                        .apply( res, content )
                        ? content
                        .asString()
                        .map( s -> !s.contains( "топилмади" )
                                ? this.getGson().fromJson( s, Insurance.class )
                                : new Insurance(
                                ErrorController
                                .getInstance()
                                        .getGetDataNotFoundErrorResponse().apply( gosno ) ) )
                        : Mono.just( new Insurance( ErrorController
                        .getInstance()
                        .getGetDataNotFoundErrorResponse().apply( gosno ) ) ); } )
            .retryWhen( Retry.backoff( 2, Duration.ofSeconds( 2 ) )
                    .doBeforeRetry( retrySignal -> LogInspector
                            .getInstance()
                            .logging( retrySignal, Methods.GET_INSURANCE ) )
                    .doAfterRetry( retrySignal -> LogInspector
                            .getInstance()
                            .logging( Methods.GET_INSURANCE, retrySignal ) )
                    .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() ) )
            .onErrorResume( io.netty.channel.ConnectTimeoutException.class,
                    throwable -> Mono.just( new Insurance( ErrorController
                            .getInstance()
                            .getGetConnectionError()
                            .apply( throwable.getMessage() ) ) ) )
            .onErrorResume( IllegalArgumentException.class,
                    throwable -> Mono.just( new Insurance( ErrorController
                            .getInstance()
                            .getGetTooManyRetriesError()
                            .apply( Methods.GET_INSURANCE ) ) ) )
            .doOnError( e -> LogInspector
                    .getInstance()
                    .logging( e, Methods.GET_INSURANCE, gosno ) )
            .doOnSuccess( value -> LogInspector
                    .getInstance()
                    .logging( Methods.GET_INSURANCE, value ) )
            .doOnSubscribe( value -> LogInspector
                    .getInstance()
                    .logging( this.getConfig().getAPI_FOR_FOR_INSURANCE() ) )
            .onErrorReturn( new Insurance( ErrorController
                    .getInstance()
                    .getGetServiceErrorResponse()
                    .apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< ModelForCar > > getVehicleData = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForGai() ) )
            .get()
            .uri( this.getConfig().getAPI_FOR_VEHICLE_DATA() + gosno )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.updateTokens().getGetVehicleData().apply( gosno );
                case 501 | 502 | 503 -> ( Mono< ModelForCar > ) ErrorController
                        .getInstance()
                        .getSaveErrorLog()
                        .apply( res.status().toString(), Methods.GET_VEHILE_DATA );
                default -> DataValidationInspector
                        .getInstance()
                        .getCheckResponse()
                        .apply( res, content )
                        ? content
                        .asString()
                        .map( s -> this.getGson().fromJson( s, ModelForCar.class ) )
                        : Mono.just( new ModelForCar( ErrorController
                        .getInstance()
                        .getGetDataNotFoundErrorResponse().apply( gosno ) ) ); } )
            .retryWhen( Retry.backoff( 2, Duration.ofSeconds( 2 ) )
                    .doBeforeRetry( retrySignal -> LogInspector
                            .getInstance()
                            .logging( retrySignal, Methods.GET_VEHILE_DATA ) )
                    .doAfterRetry( retrySignal -> LogInspector
                            .getInstance()
                            .logging( Methods.GET_VEHILE_DATA, retrySignal ) )
                    .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() ) )
            .onErrorResume( io.netty.channel.ConnectTimeoutException.class,
                    throwable -> Mono.just( new ModelForCar( ErrorController
                                        .getInstance()
                            .getGetConnectionError()
                            .apply( throwable.getMessage() ) ) ) )
            .onErrorResume( IllegalArgumentException.class,
                    throwable -> Mono.just( new ModelForCar( ErrorController
                            .getInstance()
                            .getGetTooManyRetriesError()
                            .apply( Methods.GET_VEHILE_DATA ) ) ) )
            .doOnError( e -> LogInspector
                    .getInstance()
                    .logging( e, Methods.GET_VEHILE_DATA, gosno ) )
            .doOnSuccess( value -> LogInspector
                    .getInstance()
                    .logging( Methods.GET_VEHILE_DATA, value ) )
            .doOnSubscribe( value -> LogInspector
                    .getInstance()
                    .logging( this.getConfig().getAPI_FOR_VEHICLE_DATA() ) )
            .onErrorReturn( new ModelForCar( ErrorController
                    .getInstance()
                    .getGetServiceErrorResponse()
                    .apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< Tonirovka > > getVehicleTonirovka = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForGai() ) )
            .get()
            .uri( this.getConfig().getAPI_FOR_TONIROVKA() + gosno )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.updateTokens().getGetVehicleTonirovka().apply( gosno );
                case 501 | 502 | 503 -> ( Mono< Tonirovka > ) ErrorController
                        .getInstance()
                        .getSaveErrorLog()
                        .apply( res.status().toString(), Methods.GET_TONIROVKA );
                default -> DataValidationInspector
                        .getInstance()
                        .getCheckResponse()
                        .apply( res, content )
                        ? content
                        .asString()
                        .map( s -> this.getGson().fromJson( s, Tonirovka.class ) )
                        : Mono.just( new Tonirovka( ErrorController
                        .getInstance()
                        .getGetDataNotFoundErrorResponse().apply( gosno ) ) ); } )
            .retryWhen( Retry.backoff( 2, Duration.ofSeconds( 2 ) )
                    .doBeforeRetry( retrySignal -> LogInspector
                            .getInstance()
                            .logging( retrySignal, Methods.GET_TONIROVKA ) )
                    .doAfterRetry( retrySignal -> LogInspector
                            .getInstance()
                            .logging( Methods.GET_TONIROVKA, retrySignal ) )
                    .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() ) )
            .onErrorResume( io.netty.channel.ConnectTimeoutException.class,
                    throwable -> Mono.just( new Tonirovka( ErrorController
                            .getInstance()
                            .getGetConnectionError()
                            .apply( throwable.getMessage() ) ) ) )
            .onErrorResume( IllegalArgumentException.class,
                    throwable -> Mono.just( new Tonirovka( ErrorController
                            .getInstance()
                            .getGetTooManyRetriesError()
                            .apply( Methods.GET_TONIROVKA ) ) ) )
            .doOnError( e -> LogInspector
                    .getInstance()
                    .logging( e, Methods.GET_TONIROVKA, gosno ) )
            .doOnSuccess( value -> LogInspector
                    .getInstance()
                    .logging( Methods.GET_TONIROVKA, value ) )
            .doOnSubscribe( value -> LogInspector
                    .getInstance()
                    .logging( this.getConfig().getAPI_FOR_TONIROVKA() ) )
            .onErrorReturn( new Tonirovka( ErrorController
                    .getInstance()
                    .getGetServiceErrorResponse()
                    .apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< ViolationsList > > getViolationList = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForGai() ) )
            .get()
            .uri( this.getConfig().getAPI_FOR_VIOLATION_LIST() + gosno )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.updateTokens().getGetViolationList().apply( gosno );
                case 501 | 502 | 503 -> ( Mono< ViolationsList > ) ErrorController
                        .getInstance()
                        .getSaveErrorLog()
                        .apply( res.status().toString(), Methods.GET_VIOLATION_LIST );
                default -> DataValidationInspector
                        .getInstance()
                        .getCheckResponse()
                        .apply( res, content )
                        ? content
                        .asString()
                        .map( s -> new ViolationsList( this.stringToArrayList( s, ViolationsInformation[].class ) ) )
                        : Mono.just( new ViolationsList( ErrorController
                        .getInstance()
                        .getGetDataNotFoundErrorResponse().apply( gosno ) ) ); } )
            .retryWhen( Retry.backoff( 2, Duration.ofSeconds( 2 ) )
                    .doBeforeRetry( retrySignal -> LogInspector
                            .getInstance()
                            .logging( retrySignal, Methods.GET_VIOLATION_LIST ) )
                    .doAfterRetry( retrySignal -> LogInspector
                            .getInstance()
                            .logging( Methods.GET_VIOLATION_LIST, retrySignal ) )
                    .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() ) )
            .onErrorResume( io.netty.channel.ConnectTimeoutException.class,
                    throwable -> Mono.just( new ViolationsList(
                            ErrorController
                                    .getInstance()
                                    .getGetConnectionError()
                                    .apply( throwable.getMessage() ) ) ) )
            .onErrorResume( IllegalArgumentException.class,
                    throwable -> Mono.just( new ViolationsList(
                            ErrorController
                                    .getInstance()
                                    .getGetTooManyRetriesError()
                                    .apply( Methods.GET_VIOLATION_LIST ) ) ) )
            .doOnError( e -> LogInspector
                    .getInstance()
                    .logging( e, Methods.GET_VIOLATION_LIST, gosno ) )
            .doOnSuccess( value -> LogInspector
                    .getInstance()
                    .logging( Methods.GET_VIOLATION_LIST, value ) )
            .doOnSubscribe( value -> LogInspector
                    .getInstance()
                    .logging( this.getConfig().getAPI_FOR_VIOLATION_LIST() ) )
            .onErrorReturn( new ViolationsList( ErrorController
                    .getInstance()
                    .getGetServiceErrorResponse()
                    .apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< DoverennostList > > getDoverennostList = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForGai() ) )
            .get()
            .uri( this.getConfig().getAPI_FOR_DOVERENNOST_LIST() + gosno )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.updateTokens().getGetDoverennostList().apply( gosno );
                case 501 | 502 | 503 -> ( Mono< DoverennostList > ) ErrorController
                        .getInstance()
                        .getSaveErrorLog()
                        .apply( res.status().toString(), Methods.GET_DOVERENNOST_LIST );
                default -> DataValidationInspector
                        .getInstance()
                        .getCheckResponse()
                        .apply( res, content )
                        ? content
                        .asString()
                        .map( s -> new DoverennostList( this.stringToArrayList( s, Doverennost[].class ) ) )
                        : Mono.just( new DoverennostList( ErrorController
                        .getInstance()
                        .getGetDataNotFoundErrorResponse().apply( gosno ) ) ); } )
            .retryWhen( Retry.backoff( 2, Duration.ofSeconds( 2 ) )
                    .doBeforeRetry( retrySignal -> LogInspector
                    .getInstance()
                    .logging( retrySignal, Methods.GET_DOVERENNOST_LIST ) )
                    .doAfterRetry( retrySignal -> LogInspector
                            .getInstance()
                            .logging( Methods.GET_DOVERENNOST_LIST, retrySignal ) )
                    .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() ) )
            .onErrorResume( io.netty.channel.ConnectTimeoutException.class,
                    throwable -> Mono.just( new DoverennostList( ErrorController
                            .getInstance()
                            .getGetConnectionError()
                            .apply( throwable.getMessage() ) ) ) )
            .onErrorResume( IllegalArgumentException.class,
                    throwable -> Mono.just( new DoverennostList(
                            ErrorController
                                    .getInstance()
                                    .getGetTooManyRetriesError()
                                    .apply( Methods.GET_DOVERENNOST_LIST ) ) ) )
            .doOnError( e -> LogInspector
                    .getInstance()
                    .logging( e, Methods.GET_DOVERENNOST_LIST, gosno ) )
            .doOnSuccess( value -> LogInspector
                    .getInstance()
                    .logging( Methods.GET_DOVERENNOST_LIST, value ) )
            .doOnSubscribe( value -> LogInspector
                    .getInstance()
                    .logging( this.getConfig().getAPI_FOR_DOVERENNOST_LIST() ) )
            .onErrorReturn( new DoverennostList( ErrorController
                    .getInstance()
                    .getGetServiceErrorResponse()
                    .apply( gosno ) ) );

    private final Function< String, Mono< ModelForCarList > > getModelForCarList = pinfl -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForGai() ) )
            .get()
            .uri( this.getConfig().getAPI_FOR_MODEL_FOR_CAR_LIST() + pinfl )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.updateTokens().getModelForCarList.apply( pinfl );
                case 501 | 502 | 503 -> ( Mono< ModelForCarList > ) ErrorController
                        .getInstance()
                        .getSaveErrorLog()
                        .apply( res.status().toString(), Methods.GET_MODEL_FOR_CAR_LIST );
                default -> DataValidationInspector
                        .getInstance()
                        .getCheckResponse()
                        .apply( res, content )
                        ? content
                        .asString()
                        .map( s -> new ModelForCarList( this.stringToArrayList( s, ModelForCar[].class ) ) )
                        : Mono.just( new ModelForCarList( ErrorController
                        .getInstance()
                        .getGetDataNotFoundErrorResponse()
                        .apply( pinfl ) ) ); } )
            .retryWhen( Retry.backoff( 2, Duration.ofSeconds( 2 ) )
                    .doBeforeRetry( retrySignal -> LogInspector
                    .getInstance()
                    .logging( retrySignal, Methods.GET_MODEL_FOR_CAR_LIST ) )
                    .doAfterRetry( retrySignal -> LogInspector
                    .getInstance()
                    .logging( Methods.GET_MODEL_FOR_CAR_LIST, retrySignal ) )
                    .onRetryExhaustedThrow( ( retryBackoffSpec, retrySignal ) -> new IllegalArgumentException() ) )
            .onErrorResume( io.netty.channel.ConnectTimeoutException.class,
                    throwable -> Mono.just( new ModelForCarList(
                            ErrorController
                                    .getInstance()
                                    .getGetConnectionError()
                                    .apply( throwable.getMessage() ) ) ) )
            .onErrorResume( IllegalArgumentException.class,
                    throwable -> Mono.just( new ModelForCarList(
                            ErrorController
                                    .getInstance()
                                    .getGetTooManyRetriesError()
                                    .apply( Methods.GET_MODEL_FOR_CAR_LIST ) ) ) )
            .doOnError( e -> LogInspector
                    .getInstance()
                    .logging( e, Methods.GET_MODEL_FOR_CAR_LIST, pinfl ) )
            .doOnSuccess( value -> LogInspector
                    .getInstance()
                    .logging( Methods.GET_MODEL_FOR_CAR_LIST, value ) )
            .doOnSubscribe( value -> LogInspector
                    .getInstance()
                    .logging( this.getConfig().getAPI_FOR_MODEL_FOR_CAR_LIST() ) )
            .onErrorReturn( new ModelForCarList( ErrorController
                    .getInstance()
                    .getGetServiceErrorResponse()
                    .apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< PsychologyCard, Mono< PsychologyCard > > findAllDataAboutCar = psychologyCard ->
            DataValidationInspector
                    .getInstance()
                    .getCheckData()
                    .apply( 1, psychologyCard )
                    ? Flux.fromStream( psychologyCard
                            .getModelForCarList()
                            .getModelForCarList()
                            .stream() )
                    .parallel( psychologyCard
                            .getModelForCarList()
                            .getModelForCarList()
                            .size() )
                    .runOn( Schedulers.parallel() )
                    .flatMap( modelForCar -> Mono.zip(
                            this.getInsurance().apply( modelForCar.getPlateNumber() ),
                                    Mono.just( new Tonirovka() ),
                            this.getGetDoverennostList().apply( modelForCar.getPlateNumber() ) )
                            .map( tuple3 -> modelForCar.save( tuple3, psychologyCard ) ) )
                    .sequential()
                    .publishOn( Schedulers.single() )
                    .take( 1 )
                    .single()
                    : Mono.just( psychologyCard );

    private final Function< PsychologyCard, Mono< PsychologyCard > > setPersonPrivateDataAsync = psychologyCard ->
            DataValidationInspector
                    .getInstance()
                    .getCheckData()
                    .apply( 0, psychologyCard )
                    ? this.getGetCadaster()
                    .apply( psychologyCard.getPinpp().getCadastre() )
                    .flatMap( data -> DataValidationInspector
                            .getInstance()
                            .getCheckData()
                            .apply( 2, psychologyCard.save( data ) )
                            ? Flux.fromStream( psychologyCard
                                    .getModelForCadastr()
                                    .getPermanentRegistration()
                                    .stream() )
                            .parallel( psychologyCard
                                    .getModelForCadastr()
                                    .getPermanentRegistration()
                                    .size() )
                            .runOn( Schedulers.parallel() )
                            .filter( person -> person
                                    .getPDateBirth()
                                    .equals( psychologyCard
                                            .getPinpp()
                                            .getBirthDate() ) )
                            .sequential()
                            .publishOn( Schedulers.single() )
                            .take( 1 )
                            .single()
                            .flatMap( person -> Mono.zip(
                                    this.getGetModelForAddress().apply( person.getPCitizen() ),
                                    this.getGetModelForPassport().apply( person.getPPsp(), person.getPDateBirth() ) ) )
                            .map( psychologyCard::save )
                            .onErrorResume( throwable -> Mono.just( new PsychologyCard(
                                    ErrorController
                                            .getInstance()
                                            .getGetServiceErrorResponse()
                                            .apply( throwable.getMessage() ) ) ) )
                            : Mono.just( psychologyCard ) )
                    : Mono.just( psychologyCard );

    public Mono< PsychologyCard > getPsychologyCard ( PsychologyCard psychologyCard,
                                                      String token,
                                                      ApiResponseModel apiResponseModel ) {
        try { this.getHeaders().put( "Authorization", "Bearer " + token );
            psychologyCard.setForeignerList(
                    this.stringToArrayList( Unirest.get(
                            this.getConfig().getAPI_FOR_TRAIN_TICKET_CONSUMER_SERVICE() +
                                    psychologyCard
                                            .getPapilonData()
                                            .get( 0 )
                                            .getPassport() )
                            .headers( this.getHeaders() )
                            .asJson()
                            .getBody()
                            .getObject()
                            .get( "data" )
                            .toString(), Foreigner[].class ) );
            this.getSaveUserUsageLog().apply( psychologyCard, apiResponseModel );
        } catch ( Exception e ) {
            ErrorController
                    .getInstance()
                    .saveErrorLog(
                            Methods.GET_PSYCHOLOGY_CARD_FOR_FOREIGNERS.name(),
                            psychologyCard
                                .getPapilonData()
                                .get( 0 )
                                .getPassport(),
                            Errors.DATA_NOT_FOUND.name() );
            return Mono.just( psychologyCard ); }
        return Mono.just( psychologyCard ); }

    private final Function< FIO, Mono< PersonTotalDataByFIO > > getPersonTotalDataByFIO = fio -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForFio() ) )
            .post()
            .uri( this.getConfig().getAPI_FOR_PERSON_DATA_FROM_ZAKS() )
            .send( ByteBufFlux.fromString( Mono.just( this.getGson().toJson( new RequestForFio( fio ) ) ) ) )
            .responseSingle( ( res, content ) -> res.status().code() == 401
                    ? this.updateTokens().getGetPersonTotalDataByFIO().apply( fio )
                    : DataValidationInspector
                    .getInstance()
                    .getCheckResponse()
                    .apply( res, content )
                    ? content
                    .asString()
                    .map( s -> {
                        PersonTotalDataByFIO person = this.getGson()
                                .fromJson( s, PersonTotalDataByFIO.class );
                        if ( person != null && person.getData().size() > 0 ) person
                                .getData()
                                .parallelStream()
                                .forEach( person1 -> this.getGetImageByPinfl()
                                        .apply( person1.getPinpp() )
                                        .subscribe( person1::setPersonImage ) );
                        return person != null ? person : new PersonTotalDataByFIO(); } )
                    : Mono.just( new PersonTotalDataByFIO( ErrorController
                    .getInstance()
                    .getGetDataNotFoundErrorResponse().apply( fio.getName() ) ) ) )
            .doOnError( e -> LogInspector
                    .getInstance()
                    .logging( e, Methods.GET_DATA_BY_FIO, fio.getName() ) )
            .onErrorReturn( new PersonTotalDataByFIO( ErrorController
                    .getInstance()
                    .getGetServiceErrorResponse()
                    .apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< ApiResponseModel, Mono< PsychologyCard > > getPsychologyCardByPinfl =
            apiResponseModel -> DataValidationInspector
                    .getInstance()
                    .getCheckParam()
                    .test( apiResponseModel.getStatus().getMessage() )
                    ? Mono.zip(
                            this.getGetPinpp().apply( apiResponseModel.getStatus().getMessage() ),
                            this.getGetImageByPinfl().apply( apiResponseModel.getStatus().getMessage() ),
                            this.getGetModelForCarList().apply( apiResponseModel.getStatus().getMessage() ),
                            FindFaceComponent
                                    .getInstance()
                                    .getViolationListByPinfl( apiResponseModel.getStatus().getMessage() )
                                    .onErrorReturn( new ArrayList() ) )
                    .map( PsychologyCard::new )
                    .flatMap( psychologyCard -> Mono.zip(
                            this.getFindAllDataAboutCar().apply( psychologyCard ),
                            this.getSetPersonPrivateDataAsync().apply( psychologyCard ) )
                            .map( tuple1 -> this.getSaveUserUsageLog().apply( psychologyCard, apiResponseModel ) ) )
                    : Mono.just( new PsychologyCard( ErrorController
                    .getInstance()
                    .getGetServiceErrorResponse()
                    .apply( Errors.WRONG_PARAMS.name() ) ) );

    private final BiFunction< Results, ApiResponseModel, Mono< PsychologyCard > > getPsychologyCardByImage =
            ( results, apiResponseModel ) -> Mono.zip(
                    this.getGetPinpp().apply( results
                            .getResults()
                            .get( 0 )
                            .getPersonal_code() ),
                    this.getGetImageByPinfl().apply( results
                            .getResults()
                            .get( 0 )
                            .getPersonal_code() ),
                    this.getGetModelForCarList().apply( results
                            .getResults()
                            .get( 0 )
                            .getPersonal_code() ) )
                    .map( tuple -> new PsychologyCard( results, tuple ) )
                    .flatMap( psychologyCard -> Mono.zip(
                            this.getFindAllDataAboutCar().apply( psychologyCard ),
                            this.getSetPersonPrivateDataAsync().apply( psychologyCard ) )
                            .map( tuple1 -> this.getSaveUserUsageLog().apply( psychologyCard, apiResponseModel ) ) );

    private final BiFunction< com.ssd.mvd.entity.modelForPassport.ModelForPassport, ApiResponseModel, Mono< PsychologyCard > >
            getPsychologyCardByData = ( data, apiResponseModel ) -> DataValidationInspector
                .getInstance()
                .getCheckPassport()
                .test( data )
                ? Mono.zip(
                        this.getGetPinpp().apply( data.getData().getPerson().getPinpp() ),
                        this.getGetImageByPinfl().apply( data.getData().getPerson().getPinpp() ),
                        this.getGetModelForCarList().apply( data.getData().getPerson().getPinpp() ),
                        this.getGetModelForAddress().apply( data.getData().getPerson().getPCitizen() ),
                        FindFaceComponent
                                .getInstance()
                                .getViolationListByPinfl( data.getData().getPerson().getPinpp() )
                                .onErrorReturn( new ArrayList() ) )
                .map( tuple -> new PsychologyCard( data, tuple ) )
                .flatMap( psychologyCard -> this.getFindAllDataAboutCar().apply( psychologyCard )
                        .map( psychologyCard1 -> this.getSaveUserUsageLog().apply( psychologyCard, apiResponseModel ) ) )
                : Mono.just( new PsychologyCard( ErrorController
                        .getInstance()
                        .getGetDataNotFoundErrorResponse().apply( Errors.DATA_NOT_FOUND.name() ) ) );

    @Override
    public void run () {
        while ( serDes != null ) {
            this.updateTokens();
            try { TimeUnit.HOURS.sleep( 3 ); } catch ( InterruptedException e ) { e.printStackTrace(); } } }
}