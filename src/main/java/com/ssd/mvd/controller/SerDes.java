package com.ssd.mvd.controller;

import java.util.*;
import java.util.function.*;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import io.netty.handler.logging.LogLevel;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
import com.ssd.mvd.kafka.Notification;
import com.ssd.mvd.entity.modelForGai.*;
import com.ssd.mvd.entity.family.Family;
import com.ssd.mvd.kafka.KafkaDataControl;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.entityForLogging.ErrorLog;
import com.ssd.mvd.entity.foreigner.Foreigner;
import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.entity.modelForCadastr.Data;
import com.ssd.mvd.entityForLogging.UserRequest;
import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
import com.ssd.mvd.entityForLogging.IntegratedServiceApis;
import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.modelForFioOfPerson.PersonTotalDataByFIO;

@Slf4j
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
            .headers( h -> h.add( "Content-Type", "application/json" ) )
            .wiretap( "reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL );
    private final Notification notification = new Notification();

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

    private void updateTokens () {
        log.info( "Updating tokens..." );
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
//            this.setTokenForFio(
//                    String.valueOf( Unirest.post( this.getConfig().getAPI_FOR_FIO_TOKEN() )
//                            .header("Content-Type", "application/json" )
//                            .body("{\r\n    \"Login\": \"" + this.getConfig().getLOGIN_FOR_FIO_TOKEN()
//                                    + "\",\r\n    \"Password\": \"" + this.getConfig().getPASSWORD_FOR_FIO_TOKEN()
//                                    + "\",\r\n    \"CurrentSystem\": \"" + this.getConfig().getCURRENT_SYSTEM_FOR_FIO() + "\"\r\n}")
//                            .asJson()
//                            .getBody()
//                            .getObject()
//                            .get( "access_token" ) ) );
            this.setFlag( true );
        } catch ( UnirestException e ) {
            this.setFlag( false );
            this.sendErrorLog( "updateToken", "access_token", "Error: " + e.getMessage() );
            this.saveErrorLog( e.getMessage(),
                    IntegratedServiceApis.OVIR.getName(),
                    IntegratedServiceApis.OVIR.getDescription() );
            log.error( "Error in updating tokens: " + e.getMessage() );
            this.updateTokens(); } }

    // используется когда внешние сервисы возвращают 500 ошибку
    private final Function< String, ErrorResponse > getExternalServiceErrorResponse = error -> ErrorResponse
            .builder()
            .message( "Error in external service: " + error )
            .errors( Errors.EXTERNAL_SERVICE_500_ERROR )
            .build();

    // используется когда сам сервис ловит ошибку при выполнении
    private final Function< String, ErrorResponse > getServiceErrorResponse = error -> ErrorResponse
            .builder()
            .message( "Service error: " + error )
            .errors( Errors.SERVICE_WORK_ERROR )
            .build();

    // используется когда сервис возвращает пустое тело при запросе
    private final Function< String, ErrorResponse > getDataNotFoundErrorResponse = error -> ErrorResponse
            .builder()
            .message( "Data for: " + error )
            .errors( Errors.DATA_NOT_FOUND )
            .build();

    // логирует любые ошибки
    private void sendErrorLog ( String methodName,
                                String params,
                                String reason ) {
        this.getNotification().setPinfl( params );
        this.getNotification().setReason( reason );
        this.getNotification().setMethodName( methodName );
        this.getNotification().setCallingTime( new Date() );
        KafkaDataControl
                .getInstance()
                .getWriteErrorLog()
                .accept( this.getGson().toJson( this.getNotification() ) ); }

    // отправляет ошибку на сервис Шамсиддина, в случае если какой - либо сервис не отвечает
    private void saveErrorLog ( String errorMessage,
                                String integratedService,
                                String integratedServiceDescription ) {
        KafkaDataControl
                .getInstance()
                .getWriteToKafkaErrorLog()
                .accept( this.getGson().toJson(
                        ErrorLog
                                .builder()
                                .errorMessage( errorMessage )
                                .createdAt( new Date().getTime() )
                                .integratedService( integratedService )
                                .integratedServiceApiDescription( integratedServiceDescription )
                                .build() ) ); }

    // сохраняем логи о пользователе который отправил запрос на сервис
    private final Consumer< UserRequest > saveUserUsageLog = userRequest -> Mono.fromCallable(
            () -> { KafkaDataControl
                    .getInstance()
                    .getWriteToKafkaServiceUsage()
                    .accept( this.getGson().toJson( userRequest ) );
                return Void.TYPE; } )
            .subscribeOn( Schedulers.boundedElastic() )
            .then()
            .subscribe();

    private void logging ( Throwable throwable, Methods method ) { log.error( "Error in {}: {}", method, throwable ); }

    private void logging ( Methods method, Object o ) { log.info( "Method {} has completed successfully {}", method, o ); }

    private void logging ( String method ) { log.info( method + " was cancelled" ); }

    private final Function< String, String > base64ToLink = base64 -> {
        this.getFields().clear();
        HttpResponse< JsonNode > response;
        this.getFields().put( "photo", base64 );
        this.getFields().put( "serviceName", "psychologyCard" );
        try { log.info( "Converting image to Link in: " + Methods.CONVERT_BASE64_TO_LINK );
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
            log.error( e.getMessage() );
            this.sendErrorLog( this.getConfig().getBASE64_IMAGE_TO_LINK_CONVERTER_API(),
                    Methods.CONVERT_BASE64_TO_LINK.name(),
                    "Error: " + e.getMessage() );
            return Errors.SERVICE_WORK_ERROR.name(); } };

    private final Function< String, Mono< Pinpp > > getPinpp = pinpp -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForPassport() ) )
            .get()
            .uri( this.getConfig().getAPI_FOR_PINPP() + pinpp )
            .responseSingle( ( res, content ) -> {
                log.info( "Pinpp: " + pinpp );
                if ( res.status().code() == 401 ) {
                    this.updateTokens();
                    return this.getGetPinpp().apply( pinpp ); }

                if ( this.check500ErrorAsync.test( res.status().code() ) ) {
                    this.saveErrorLog( res.status().toString(),
                            IntegratedServiceApis.OVIR.getName(),
                            IntegratedServiceApis.OVIR.getDescription() );
                    return Mono.just( new Pinpp(
                            this.getExternalServiceErrorResponse.apply( res.status().toString() ) ) ); }

                return res.status().code() == 200
                        && content != null
                        ? content
                        .asString()
                        .map( s -> this.getGson().fromJson( s, Pinpp.class ) )
                        : Mono.just( new Pinpp( this.getGetDataNotFoundErrorResponse().apply( pinpp ) ) ); } )
            .doOnError( e -> {
                this.logging( e, Methods.GET_PINPP );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.OVIR.getName(),
                        IntegratedServiceApis.OVIR.getDescription() );
                this.sendErrorLog( Methods.GET_PINPP.name(), pinpp, "Error in service: " + e.getMessage() ); } )
            .doOnSuccess( value -> this.logging( Methods.GET_PINPP, value ) )
            .doOnCancel( () -> this.logging( this.getConfig().getAPI_FOR_PINPP() ) );
//            .onErrorReturn( new Pinpp( this.getGetServiceErrorResponse().apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< Data > > getCadaster = cadaster -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForPassport() ) )
            .post()
            .send( ByteBufFlux.fromString( Mono.just(
                    this.getGson().toJson( new RequestForCadaster( cadaster ) ) ) ) )
            .uri( this.getConfig().getAPI_FOR_CADASTR() )
            .responseSingle( ( res, content ) -> {
                log.info( "Pcadastre in: " + Methods.CADASTER + " : " + cadaster );
                if ( res.status().code() == 401 ) {
                    this.updateTokens();
                    return this.getGetCadaster().apply( cadaster ); }

                if ( this.check500ErrorAsync.test( res.status().code() ) ) {
                    this.saveErrorLog(
                            res.status().toString(),
                            IntegratedServiceApis.OVIR.getName(),
                            IntegratedServiceApis.OVIR.getDescription() );
                    return Mono.just( new Data(
                            this.getGetExternalServiceErrorResponse().apply( res.status().toString() ) ) ); }

                return content != null
                        && res.status().code() == 200
                        ? content
                        .asString()
                        .map( s -> this.getGson().fromJson(
                                s.substring( s.indexOf( "Data" ) + 6, s.indexOf( ",\"AnswereId" ) ), Data.class ) )
                        : Mono.just( new Data( this.getGetDataNotFoundErrorResponse().apply( cadaster ) ) ); } )
            .doOnError( e -> {
                this.logging( e, Methods.CADASTER );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.OVIR.getName(),
                        IntegratedServiceApis.OVIR.getDescription() );
                this.sendErrorLog( Methods.CADASTER.name(), cadaster, "Error: " + e.getMessage() ); } )
            .doOnSuccess( value -> this.logging( Methods.CADASTER, value ) )
            .doOnCancel( () -> this.logging( this.getConfig().getAPI_FOR_CADASTR() ) )
            .onErrorReturn( new Data( this.getGetServiceErrorResponse().apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< String > > getImageByPinfl = pinfl -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForGai() ) )
            .get()
            .uri( this.getConfig().getAPI_FOR_PERSON_IMAGE() + pinfl )
            .responseSingle( ( res, content ) -> {
                log.info( "Pinfl in : " + Methods.GET_IMAGE_BY_PINFL + " : " + pinfl );
                if ( res.status().code() == 401 ) {
                    this.updateTokens();
                    return getGetImageByPinfl().apply( pinfl ); }

                if ( this.check500ErrorAsync.test( res.status().code() ) ) {
                    this.saveErrorLog(
                            res.status().toString(),
                            IntegratedServiceApis.OVIR.getName(),
                            IntegratedServiceApis.OVIR.getDescription() );
                    return Mono.just( Errors.EXTERNAL_SERVICE_500_ERROR.name() ); }

                return res.status().code() == 200
                        && content != null
                        ? content
                        .asString()
                        .map( s -> s.substring( s.indexOf( "Data" ) + 7, s.indexOf( ",\"AnswereId" ) - 1 ) )
                        : Mono.just( Errors.DATA_NOT_FOUND.name() ); } )
            .doOnError( e -> {
                this.logging( e, Methods.GET_IMAGE_BY_PINFL );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.OVIR.getName(),
                        IntegratedServiceApis.OVIR.getDescription() );
                this.sendErrorLog( Methods.GET_IMAGE_BY_PINFL.name(), pinfl, "Error: " + e.getMessage() ); } )
            .doOnCancel( () -> this.logging( this.getConfig().getAPI_FOR_PERSON_IMAGE() ) );
//            .onErrorReturn( Errors.DATA_NOT_FOUND.name() );

    private final Function< String, Mono< ModelForAddress > > getModelForAddress = pinfl -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForGai() ) )
            .post()
            .uri( this.getConfig().getAPI_FOR_MODEL_FOR_ADDRESS() )
            .send( ByteBufFlux.fromString( Mono.just( this.getGson().toJson( new RequestForModelOfAddress( pinfl ) ) ) ) )
            .responseSingle( ( res, content ) -> {
                log.info( "Pinfl in: " + Methods.GET_MODEL_FOR_ADDRESS + " : " + pinfl );
                if ( res.status().code() == 401 ) {
                    this.updateTokens();
                    return this.getGetModelForAddress().apply( pinfl ); }

                if ( this.check500ErrorAsync.test( res.status().code() ) ) {
                    this.saveErrorLog(
                            res.status().toString(),
                            IntegratedServiceApis.OVIR.getName(),
                            IntegratedServiceApis.OVIR.getDescription() );
                    return Mono.just( new ModelForAddress(
                            this.getExternalServiceErrorResponse.apply( res.status().toString() ) ) ); }

                return res.status().code() == 200
                        && content != null
                        ? content
                        .asString()
                        .map( s -> this.getGson().fromJson(
                                s.substring( s.indexOf( "Data" ) + 6, s.indexOf( ",\"AnswereId" ) ),
                                ModelForAddress.class ) )
                        : Mono.just( new ModelForAddress( this.getGetDataNotFoundErrorResponse().apply( pinfl ) ) ); } )
            .doOnError( e -> {
                this.logging( e, Methods.GET_MODEL_FOR_ADDRESS );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.OVIR.getName(),
                        IntegratedServiceApis.OVIR.getDescription() );
                this.sendErrorLog( Methods.GET_MODEL_FOR_ADDRESS.name(), pinfl, "Error: " + e.getMessage() ); } )
            .doOnSuccess( value -> this.logging( Methods.GET_MODEL_FOR_ADDRESS, value ) )
            .doOnCancel( () -> this.logging( this.getConfig().getAPI_FOR_MODEL_FOR_ADDRESS() ) )
            .onErrorReturn( new ModelForAddress(
                    this.getGetServiceErrorResponse().apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final BiFunction< String, String, Mono< com.ssd.mvd.entity.modelForPassport.ModelForPassport > > getModelForPassport =
            ( SerialNumber, BirthDate ) -> this.getHttpClient()
                    .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForPassport() ) )
                    .post()
                    .uri( this.getConfig().getAPI_FOR_PASSPORT_MODEL() )
                    .send( ByteBufFlux.fromString( Mono.just( this.getGson().toJson(
                            new RequestForPassport( SerialNumber, BirthDate ) ) ) ) )
                    .responseSingle( ( res, content ) -> {
                        if ( res.status().code() == 401 ) {
                            this.updateTokens();
                            return this.getGetModelForPassport().apply( SerialNumber, BirthDate ); }

                        if ( this.check500ErrorAsync.test( res.status().code() ) ) {
                            this.saveErrorLog(
                                    res.status().toString(),
                                    IntegratedServiceApis.OVIR.getName(),
                                    IntegratedServiceApis.OVIR.getDescription() );
                            return Mono.just( new com.ssd.mvd.entity.modelForPassport.ModelForPassport(
                                    this.getExternalServiceErrorResponse.apply( res.status().toString() ) ) ); }

                        return res.status().code() == 200
                                && content != null
                                ? content
                                .asString()
                                .map( s -> this.getGson()
                                        .fromJson( s, com.ssd.mvd.entity.modelForPassport.ModelForPassport.class ) )
                                : Mono.just( new com.ssd.mvd.entity.modelForPassport.ModelForPassport(
                                        this.getGetDataNotFoundErrorResponse()
                                                .apply( SerialNumber + " : " + SerialNumber ) ) ); } )
                    .doOnError( e -> {
                        this.logging( e, Methods.GET_MODEL_FOR_PASSPORT );
                        this.saveErrorLog( e.getMessage(),
                                IntegratedServiceApis.OVIR.getName(),
                                IntegratedServiceApis.OVIR.getDescription() );
                        this.sendErrorLog( Methods.GET_MODEL_FOR_PASSPORT.name(),
                                SerialNumber + "_" + BirthDate,
                                "Error: " + e.getMessage() ); } )
                    .doOnSuccess( value -> this.logging( Methods.GET_MODEL_FOR_PASSPORT, value ) )
                    .doOnCancel( () -> this.logging( this.getConfig().getAPI_FOR_PASSPORT_MODEL() ) )
                    .onErrorReturn( new com.ssd.mvd.entity.modelForPassport.ModelForPassport(
                            this.getGetServiceErrorResponse().apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< Insurance > > insurance = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForGai() ) )
            .get()
            .uri( this.getConfig().getAPI_FOR_FOR_INSURANCE() + gosno )
            .responseSingle( ( res, content ) -> {
                log.info( "Gosno in insurance: " + gosno
                        + " With status: " + res.status() );
                if ( res.status().code() == 401 ) {
                    this.updateTokens();
                    return this.getInsurance().apply( gosno ); }

                if ( this.check500ErrorAsync.test( res.status().code() ) ) {
                    this.saveErrorLog(
                            res.status().toString(),
                            IntegratedServiceApis.GAI.getName(),
                            IntegratedServiceApis.GAI.getDescription() );
                    return Mono.just( new Insurance(
                            this.getExternalServiceErrorResponse.apply( res.status().toString() ) ) ); }

                return res.status().code() == 200
                        && content != null
                        ? content
                        .asString()
                        .map( s -> !s.contains( "топилмади" )
                                ? this.getGson().fromJson( s, Insurance.class )
                                : new Insurance(
                                        this.getGetDataNotFoundErrorResponse().apply( Errors.DATA_NOT_FOUND.name() ) ) )
                        : Mono.just( new Insurance(
                        this.getGetDataNotFoundErrorResponse().apply( gosno ) ) ); } )
            .doOnError( e -> {
                this.logging( e, Methods.GET_INSURANCE );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.GAI.getName(),
                        IntegratedServiceApis.GAI.getDescription() );
                this.sendErrorLog( Methods.GET_INSURANCE.name(), gosno, "Error: " + e.getMessage() ); } )
            .doOnSuccess( value -> this.logging( Methods.GET_INSURANCE, value ) )
            .doOnCancel( () -> this.logging( this.getConfig().getAPI_FOR_FOR_INSURANCE() ) )
            .onErrorReturn( new Insurance( this.getGetServiceErrorResponse().apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< ModelForCar > > getVehicleData = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForGai() ) )
            .get()
            .uri( this.getConfig().getAPI_FOR_VEHICLE_DATA() + gosno )
            .responseSingle( ( res, content ) -> {
                log.info( "Gosno in getVehicleData: " + gosno
                        + " With status: " + res.status() );
                if ( res.status().code() == 401 ) {
                    this.updateTokens();
                    return this.getGetVehicleData().apply( gosno ); }

                if ( this.check500ErrorAsync.test( res.status().code() ) ) {
                    this.saveErrorLog(
                            res.status().toString(),
                            IntegratedServiceApis.GAI.getName(),
                            IntegratedServiceApis.GAI.getDescription() );
                    return Mono.just( new ModelForCar(
                            this.getExternalServiceErrorResponse.apply( res.status().toString() ) ) ); }

                return res.status().code() == 200
                        && content != null
                        ? content
                        .asString()
                        .map( s -> this.getGson().fromJson( s, ModelForCar.class ) )
                        : Mono.just( new ModelForCar( this.getGetDataNotFoundErrorResponse().apply( gosno ) ) ); } )
            .doOnError( e -> {
                this.logging( e, Methods.GET_VEHILE_DATA );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.GAI.getName(),
                        IntegratedServiceApis.GAI.getDescription() );
                this.sendErrorLog( Methods.GET_VEHILE_DATA.name(), gosno, e.getMessage() ); } )
            .doOnSuccess( value -> this.logging( Methods.GET_VEHILE_DATA, value ) )
            .doOnCancel( () -> this.logging( this.getConfig().getAPI_FOR_VEHICLE_DATA() ) )
            .onErrorReturn( new ModelForCar( this.getGetServiceErrorResponse().apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< Tonirovka > > getVehicleTonirovka = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForGai() ) )
            .get()
            .uri( this.getConfig().getAPI_FOR_TONIROVKA() + gosno )
            .responseSingle( ( res, content ) -> {
                log.info( "Gosno in getVehicleTonirovka: " + gosno
                        + " With status: " + res.status() );
                if ( res.status().code() == 401 ) {
                    this.updateTokens();
                    return this.getGetVehicleTonirovka().apply( gosno ); }

                if ( this.check500ErrorAsync.test( res.status().code() ) ) {
                    this.saveErrorLog(
                            res.status().toString(),
                            IntegratedServiceApis.GAI.getName(),
                            IntegratedServiceApis.GAI.getDescription() );
                    return Mono.just( new Tonirovka(
                            this.getExternalServiceErrorResponse.apply( res.status().toString() ) ) ); }

                return res.status().code() == 200
                        && content != null
                        ? content
                        .asString()
                        .map( s -> this.getGson().fromJson( s, Tonirovka.class ) )
                        : Mono.just( new Tonirovka( this.getGetDataNotFoundErrorResponse().apply( gosno ) ) ); } )
            .doOnError( e -> {
                this.logging( e, Methods.GET_TONIROVKA );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.GAI.getName(),
                        IntegratedServiceApis.GAI.getDescription() );
                this.sendErrorLog( Methods.GET_TONIROVKA.name(), gosno, e.getMessage() ); } )
            .doOnSuccess( value -> this.logging( Methods.GET_TONIROVKA, value ) )
            .doOnCancel( () -> this.logging( this.getConfig().getAPI_FOR_TONIROVKA() ) )
            .onErrorReturn( new Tonirovka( this.getGetServiceErrorResponse().apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< ViolationsList > > getViolationList = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForGai() ) )
            .get()
            .uri( this.getConfig().getAPI_FOR_VIOLATION_LIST() + gosno )
            .responseSingle( ( res, content ) -> {
                log.info( "Gosno in getViolationList: " + gosno
                        + " With status: " + res.status() );
                if ( res.status().code() == 401 ) {
                    this.updateTokens();
                    return this.getGetViolationList().apply( gosno ); }

                if ( this.check500ErrorAsync.test( res.status().code() ) ) {
                    this.saveErrorLog(
                            res.status().toString(),
                            IntegratedServiceApis.GAI.getName(),
                            IntegratedServiceApis.GAI.getDescription() );
                    return Mono.just( new ViolationsList(
                            this.getGetExternalServiceErrorResponse().apply( res.status().toString() ) ) ); }

                return res.status().code() == 200
                        && content != null
                        ? content
                        .asString()
                        .map( s -> new ViolationsList( this.stringToArrayList( s, ViolationsInformation[].class ) ) )
                        : Mono.just( new ViolationsList( this.getGetDataNotFoundErrorResponse().apply( gosno ) ) ); } )
            .doOnError( e -> {
                this.logging( e, Methods.GET_VIOLATION_LIST );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.GAI.getName(),
                        IntegratedServiceApis.GAI.getDescription() );
                this.sendErrorLog( Methods.GET_VIOLATION_LIST.name(), gosno, e.getMessage() ); } )
            .doOnSuccess( value -> this.logging( Methods.GET_VIOLATION_LIST, value ) )
            .doOnCancel( () -> this.logging( this.getConfig().getAPI_FOR_VIOLATION_LIST() ) )
            .onErrorReturn( new ViolationsList( this.getGetServiceErrorResponse().apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< DoverennostList > > getDoverennostList = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForGai() ) )
            .get()
            .uri( this.getConfig().getAPI_FOR_DOVERENNOST_LIST() + gosno )
            .responseSingle( ( res, content ) -> {
                log.error( "Gosno in: " + Methods.GET_DOVERENNOST_LIST + " : " + gosno
                        + " With status: " + res.status() );
                if ( res.status().code() == 401 ) {
                    this.updateTokens();
                    return this.getGetDoverennostList().apply( gosno ); }

                if ( this.check500ErrorAsync.test( res.status().code() ) ) {
                    this.saveErrorLog(
                            res.status().toString(),
                            IntegratedServiceApis.GAI.getName(),
                            IntegratedServiceApis.GAI.getDescription() );
                    return Mono.just( new DoverennostList(
                            this.getGetExternalServiceErrorResponse().apply( res.status().toString() ) ) ); }

                return res.status().code() == 200
                        && content != null
                        ? content
                        .asString()
                        .map( s -> new DoverennostList( this.stringToArrayList( s, Doverennost[].class ) ) )
                        : Mono.just( new DoverennostList( this.getGetDataNotFoundErrorResponse().apply( gosno ) ) ); } )
            .doOnError( e -> {
                this.logging( e, Methods.GET_DOVERENNOST_LIST );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.GAI.getName(),
                        IntegratedServiceApis.GAI.getDescription() );
                this.sendErrorLog( Methods.GET_DOVERENNOST_LIST.name(), gosno, "Error: " + e.getMessage() ); } )
            .doOnSuccess( value -> this.logging( Methods.GET_DOVERENNOST_LIST, value ) )
            .doOnCancel( () -> this.logging( this.getConfig().getAPI_FOR_DOVERENNOST_LIST() ) )
            .onErrorReturn( new DoverennostList( this.getGetServiceErrorResponse().apply( gosno ) ) );

    private final Function< String, Mono< ModelForCarList > > getModelForCarList = pinfl -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + this.getTokenForGai() ) )
            .get()
            .uri( this.getConfig().getAPI_FOR_MODEL_FOR_CAR_LIST() + pinfl )
            .responseSingle( ( res, content ) -> {
                if ( res.status().code() == 401 ) {
                    this.updateTokens();
                    return this.getModelForCarList.apply( pinfl ); }

                if ( this.check500ErrorAsync.test( res.status().code() ) ) {
                    this.saveErrorLog(
                            res.status().toString(),
                            IntegratedServiceApis.GAI.getName(),
                            IntegratedServiceApis.GAI.getDescription() );
                    return Mono.just( new ModelForCarList(
                            this.getGetExternalServiceErrorResponse().apply( res.status().toString() ) ) ); }

                return res.status().code() == 200
                        && content != null
                        ? content
                        .asString()
                        .map( s -> new ModelForCarList( this.stringToArrayList( s, ModelForCar[].class ) ) )
                        : Mono.just( new ModelForCarList(
                        this.getGetDataNotFoundErrorResponse().apply( pinfl ) ) ); } )
            .doOnError( e -> {
                this.logging( e, Methods.GET_MODEL_FOR_CAR_LIST );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.GAI.getName(),
                        IntegratedServiceApis.GAI.getDescription() );
                this.sendErrorLog( Methods.GET_MODEL_FOR_CAR_LIST.name(), pinfl, "Error: " + e.getMessage() ); } )
            .doOnSuccess( value -> this.logging( Methods.GET_MODEL_FOR_CAR_LIST, value ) )
            .doOnCancel( () -> this.logging( this.getConfig().getAPI_FOR_MODEL_FOR_CAR_LIST() ) );
//            .onErrorReturn( new ModelForCarList(
//                    this.getGetServiceErrorResponse().apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Predicate< Integer > check500ErrorAsync = statusCode ->
            statusCode == 500
            ^ statusCode == 501
            ^ statusCode == 502
            ^ statusCode == 503;

    private final Predicate< PsychologyCard > checkCarData = psychologyCard ->
            psychologyCard.getModelForCarList() != null
            && psychologyCard
            .getModelForCarList()
            .getModelForCarList() != null
            && psychologyCard
            .getModelForCarList()
            .getModelForCarList()
            .size() > 0;

    private final Consumer< PsychologyCard > findAllDataAboutCarAsync = psychologyCard -> {
        if ( this.getCheckCarData().test( psychologyCard ) ) psychologyCard
                .getModelForCarList()
                .getModelForCarList()
                .parallelStream()
                .forEach( modelForCar -> {
                    this.getInsurance().apply( modelForCar.getPlateNumber() )
                            .subscribe( modelForCar::setInsurance );
                    this.getGetVehicleTonirovka().apply( modelForCar.getPlateNumber() )
                            .subscribe( modelForCar::setTonirovka );
                    this.getGetDoverennostList().apply( modelForCar.getPlateNumber() )
                            .subscribe( modelForCar::setDoverennostList ); } ); };

    private final Predicate< PsychologyCard > checkPrivateData = psychologyCard ->
            psychologyCard.getModelForCadastr() != null
            && psychologyCard
            .getModelForCadastr()
            .getPermanentRegistration() != null
            && psychologyCard
            .getModelForCadastr()
            .getPermanentRegistration().size() > 0;

    private final Function< PsychologyCard, Mono< PsychologyCard > > setPersonPrivateDataAsync = psychologyCard ->
            this.getGetCadaster()
                    .apply( psychologyCard.getPinpp().getCadastre() )
                    .flatMap( data -> {
                        psychologyCard.setModelForCadastr( data );
                        return this.getCheckPrivateData().test( psychologyCard )
                                ? Flux.fromStream( psychologyCard
                                        .getModelForCadastr()
                                        .getPermanentRegistration()
                                        .stream() )
                                .parallel()
                                .runOn( Schedulers.parallel() )
                                .filter( person -> person
                                        .getPDateBirth()
                                        .equals( psychologyCard
                                                .getPinpp()
                                                .getBirthDate() ) )
                                .sequential()
                                .publishOn( Schedulers.single() )
                                .single()
                                .flatMap( person -> Mono.zip(
                                        this.getGetModelForAddress().apply( person.getPCitizen() ),
                                        this.getGetModelForPassport().apply( person.getPPsp(), person.getPDateBirth() ) ) )
                                .map( tuple1 -> {
                                    psychologyCard.setModelForPassport( tuple1.getT2() );
                                    psychologyCard.setModelForAddress( tuple1.getT1() );
                                    return psychologyCard; } )
                                : Mono.just( psychologyCard ); } );

    private final Predicate< Family > checkFamily = family ->
            family != null
            && family.getItems() != null
            && !family.getItems().isEmpty();

    private final BiFunction< Results, PsychologyCard, Mono< PsychologyCard > > findAllAboutFamily =
            ( results, psychologyCard ) -> {
                // личные данные человека чьи данные были переданы на данный сервис
                psychologyCard.setChildData( results.getChildData() );

                // личные данные матери, того чьи данные были переданы на данный сервис
                psychologyCard.setMommyData( results.getMommyData() );
                psychologyCard.setMommyPinfl( results.getMommyPinfl() );

                // личные данные отца, того чьи данные были переданы на данный сервис
                psychologyCard.setDaddyData( results.getDaddyData() );
                psychologyCard.setDaddyPinfl( results.getDaddyPinfl() );

                if ( this.getCheckFamily().test( psychologyCard.getChildData() ) ) psychologyCard
                        .getChildData()
                        .getItems()
                        .parallelStream()
                        .forEach( familyMember -> this.getGetImageByPinfl()
                                .apply( familyMember.getPnfl() )
                                .subscribe( familyMember::setPersonal_image ) );

                if ( this.getCheckFamily().test( psychologyCard.getDaddyData() ) ) psychologyCard
                        .getDaddyData()
                        .getItems()
                        .parallelStream()
                        .forEach( familyMember -> this.getGetImageByPinfl()
                                .apply( familyMember.getPnfl() )
                                .subscribe( familyMember::setPersonal_image ) );

                if ( this.getCheckFamily().test( psychologyCard.getMommyData() ) ) psychologyCard
                        .getMommyData()
                        .getItems()
                        .parallelStream()
                        .forEach( familyMember -> this.getGetImageByPinfl()
                                .apply( familyMember.getPnfl() )
                                .subscribe( familyMember::setPersonal_image ) );
                return Mono.just( psychologyCard ); };

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
            this.getSaveUserUsageLog().accept( new UserRequest( psychologyCard, apiResponseModel ) );
        } catch ( Exception e ) {
            this.sendErrorLog( "getPsychologyCard",
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
            .responseSingle( ( res, content ) -> {
                if ( res.status().code() == 401 ) {
                    this.updateTokens();
                    return this.getGetPersonTotalDataByFIO().apply( fio ); }

                if ( this.check500ErrorAsync.test( res.status().code() ) ) this.saveErrorLog(
                        res.status().toString(),
                        IntegratedServiceApis.GAI.getName(),
                        IntegratedServiceApis.GAI.getDescription() );

                return res.status().code() == 200
                        && content != null
                        ? content
                        .asString()
                        .map( s -> {
                            PersonTotalDataByFIO person = this.getGson()
                                    .fromJson( s, PersonTotalDataByFIO.class );
                            if ( person != null && person.getData().size() > 0 ) {
                                person
                                        .getData()
                                        .parallelStream()
                                        .forEach( person1 -> this.getGetImageByPinfl()
                                                .apply( person1.getPinpp() )
                                                .subscribe( person1::setPersonImage ) );
                                this.getSaveUserUsageLog().accept( new UserRequest( person, fio ) ); }
                            return person != null ? person : new PersonTotalDataByFIO(); } )
                        : Mono.just( new PersonTotalDataByFIO(
                        this.getGetDataNotFoundErrorResponse().apply( fio.getName() ) ) ); } )
            .doOnError( e -> {
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.GAI.getName(),
                        IntegratedServiceApis.GAI.getDescription() );
                this.sendErrorLog( "getPersonTotalDataByFIO", fio.getName(), "Error: " + e.getMessage() ); } )
            .onErrorReturn( new PersonTotalDataByFIO(
                    this.getGetServiceErrorResponse().apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< ApiResponseModel, Mono< PsychologyCard > > getPsychologyCardByPinfl =
            apiResponseModel -> apiResponseModel.getStatus().getMessage() != null
                    ? Mono.zip(
                            this.getGetPinpp().apply( apiResponseModel.getStatus().getMessage() ),
                            this.getGetImageByPinfl().apply( apiResponseModel.getStatus().getMessage() ),
                            this.getGetModelForCarList().apply( apiResponseModel.getStatus().getMessage() ),
                            FindFaceComponent
                                    .getInstance()
                                    .getViolationListByPinfl( apiResponseModel.getStatus().getMessage() )
                                    .onErrorContinue( ( error, object ) -> log.error( "Error: {} and reason: {}: ",
                                            error.getMessage(), object ) )
                                    .onErrorReturn( new ArrayList() ),
                            FindFaceComponent
                                    .getInstance()
                                    .getFamilyMembersData( apiResponseModel.getStatus().getMessage() ) )
                    .flatMap( tuple -> {
                        PsychologyCard psychologyCard = new PsychologyCard( tuple );
                        this.getFindAllDataAboutCarAsync().accept( psychologyCard );
                        this.getFindAllAboutFamily().apply( tuple.getT5(), psychologyCard );
                        this.getSaveUserUsageLog().accept( new UserRequest( psychologyCard, apiResponseModel ) );
                        return this.getSetPersonPrivateDataAsync().apply( psychologyCard ); } )
                    : Mono.just( new PsychologyCard( this.getGetServiceErrorResponse().apply( Errors.WRONG_PARAMS.name() ) ) );

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
                    .flatMap( psychologyCard -> {
                        this.getFindAllDataAboutCarAsync().accept( psychologyCard );
                        this.getFindAllAboutFamily().apply( results, psychologyCard );
                        return this.getSetPersonPrivateDataAsync().apply( psychologyCard ); } );

    private final BiFunction< com.ssd.mvd.entity.modelForPassport.ModelForPassport, ApiResponseModel, Mono< PsychologyCard > >
            getPsychologyCardByData = ( data, apiResponseModel ) -> data.getData().getPerson() != null
            ? Mono.zip(
                    this.getGetPinpp().apply( data.getData().getPerson().getPinpp() ),
                    this.getGetImageByPinfl().apply( data.getData().getPerson().getPinpp() ),
                    this.getGetModelForCarList().apply( data.getData().getPerson().getPinpp() ),
                    this.getGetModelForAddress().apply( data.getData().getPerson().getPCitizen() ),
                    FindFaceComponent
                            .getInstance()
                            .getViolationListByPinfl( data.getData().getPerson().getPinpp() )
                            .onErrorContinue( ( error, object ) -> log.error( "Error: {} and reason: {}: ",
                                    error.getMessage(), object ) )
                            .onErrorReturn( new ArrayList() ),
                    FindFaceComponent
                            .getInstance()
                            .getFamilyMembersData( data.getData().getPerson().getPinpp() )
                            .onErrorContinue( ( error, object ) -> log.error( "Error: {} and reason: {}: ",
                                    error.getMessage(), object ) )
                            .onErrorReturn( new Results(
                                    this.getGetServiceErrorResponse().apply( Errors.SERVICE_WORK_ERROR.name() ) ) ) )
            .flatMap( tuple -> {
                PsychologyCard psychologyCard = new PsychologyCard( data, tuple );
                this.getFindAllDataAboutCarAsync().accept( psychologyCard );
                this.getFindAllAboutFamily().apply( tuple.getT6(), psychologyCard );
                return this.getSetPersonPrivateDataAsync().apply( psychologyCard ); } )
            : Mono.just( new PsychologyCard( this.getGetDataNotFoundErrorResponse().apply( Errors.DATA_NOT_FOUND.name() ) ) );

    @Override
    public void run () {
        while ( serDes != null ) {
            this.updateTokens();
            try { TimeUnit.HOURS.sleep( 3 ); } catch ( InterruptedException e ) { e.printStackTrace(); } } }
}