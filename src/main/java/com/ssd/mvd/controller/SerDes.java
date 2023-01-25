package com.ssd.mvd.controller;

import java.util.*;
import java.time.Duration;
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
            .responseTimeout( Duration.ofMinutes( 1 ) )
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
    private final Consumer< UserRequest > saveUserUsageLog = userRequest -> KafkaDataControl
            .getInstance()
            .getWriteToKafkaServiceUsage()
            .accept( this.getGson().toJson( userRequest ) );

    private void logging ( Throwable throwable, Methods method ) { log.error( "Error in {}: {}", method, throwable ); }

    private void logging ( Methods method, Object o ) { log.info( "Method {} has completed successfully {}", method, o ); }

    private void logging ( String method ) { log.info( method + " has subscribed" ); }

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
            .doOnSubscribe( value -> this.logging( this.getConfig().getAPI_FOR_PINPP() ) )
            .onErrorReturn( new Pinpp( this.getGetServiceErrorResponse().apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

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
            .doOnSubscribe( value -> this.logging( this.getConfig().getAPI_FOR_CADASTR() ) )
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
            .doOnSubscribe( value -> this.logging( this.getConfig().getAPI_FOR_PERSON_IMAGE() ) )
            .onErrorReturn( Errors.DATA_NOT_FOUND.name() );

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
            .doOnSubscribe( value -> this.logging( this.getConfig().getAPI_FOR_MODEL_FOR_ADDRESS() ) )
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
                    .doOnSubscribe( value -> this.logging( this.getConfig().getAPI_FOR_PASSPORT_MODEL() ) )
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
            .doOnSubscribe( value -> this.logging( this.getConfig().getAPI_FOR_FOR_INSURANCE() ) )
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
            .doOnSubscribe( value -> this.logging( this.getConfig().getAPI_FOR_VEHICLE_DATA() ) )
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
            .doOnSubscribe( value -> this.logging( this.getConfig().getAPI_FOR_TONIROVKA() ) )
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
            .doOnSubscribe( value -> this.logging( this.getConfig().getAPI_FOR_VIOLATION_LIST() ) )
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
            .doOnSubscribe( value -> this.logging( this.getConfig().getAPI_FOR_DOVERENNOST_LIST() ) )
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
            .doOnSubscribe( value -> this.logging( this.getConfig().getAPI_FOR_MODEL_FOR_CAR_LIST() ) )
            .onErrorReturn( new ModelForCarList(
                    this.getGetServiceErrorResponse().apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

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

//    private final Function< PsychologyCard, Mono< PsychologyCard > > findAllDataAboutCar = psychologyCard ->
//            this.getCheckCarData().test( psychologyCard )
//                    ? Flux.fromStream( psychologyCard
//                            .getModelForCarList()
//                            .getModelForCarList()
//                            .stream() )
//                    .parallel( psychologyCard
//                            .getModelForCarList()
//                            .getModelForCarList()
//                            .size() )
//                    .runOn( Schedulers.parallel() )
//                    .flatMap( modelForCar -> Mono.zip( this.getInsurance().apply( modelForCar.getPlateNumber() ),
//                                    this.getGetVehicleTonirovka().apply( modelForCar.getPlateNumber() ),
//                                    this.getGetDoverennostList().apply( modelForCar.getPlateNumber() ) )
//                            .map( tuple -> {
//                                modelForCar.setDoverennostList( tuple.getT3() );
//                                modelForCar.setInsurance( tuple.getT1() );
//                                modelForCar.setTonirovka( tuple.getT2() );
//                                return psychologyCard; } ) )
//                    .sequential()
//                    .publishOn( Schedulers.single() )
//                    .take( 1 )
//                    .single()
//                    : Mono.just( psychologyCard );

    private final Function< PsychologyCard, Mono< PsychologyCard > > findAllDataAboutCar = psychologyCard ->
            this.getCheckCarData().test( psychologyCard )
                    ? Flux.fromStream( psychologyCard
                            .getModelForCarList()
                            .getModelForCarList()
                            .stream() )
                    .parallel( psychologyCard
                            .getModelForCarList()
                            .getModelForCarList()
                            .size() )
                    .runOn( Schedulers.parallel() )
                    .flatMap( modelForCar -> this.getInsurance().apply( modelForCar.getPlateNumber() )
                            .map( insurance1 -> {
                                modelForCar.setInsurance( insurance1 );
                                return modelForCar; } ) )
                    .flatMap( modelForCar -> this.getGetVehicleTonirovka().apply( modelForCar.getPlateNumber() )
                            .map( tonirovka -> {
                                modelForCar.setTonirovka( tonirovka );
                                return modelForCar; } ) )
                    .flatMap( modelForCar -> this.getGetDoverennostList().apply( modelForCar.getPlateNumber() )
                            .map( doverennostList -> {
                                modelForCar.setDoverennostList( doverennostList );
                                return psychologyCard; } ) )
                    .sequential()
                    .publishOn( Schedulers.single() )
                    .take( 1 )
                    .single()
                    : Mono.just( psychologyCard );

    private final Predicate< PsychologyCard > checkPrivateData = psychologyCard ->
            psychologyCard.getModelForCadastr() != null
            && psychologyCard
            .getModelForCadastr()
            .getPermanentRegistration() != null
            && psychologyCard
            .getModelForCadastr()
            .getPermanentRegistration().size() > 0;

    private final Function< PsychologyCard, Mono< PsychologyCard > > setPersonPrivateDataAsync = psychologyCard ->
            psychologyCard.getPinpp() != null
            && psychologyCard.getPinpp().getCadastre() != null
            && psychologyCard.getPinpp().getCadastre().length() > 1
                    ? this.getGetCadaster()
                    .apply( psychologyCard.getPinpp().getCadastre() )
                    .flatMap( data -> {
                        psychologyCard.setModelForCadastr( data );
                        return this.getCheckPrivateData().test( psychologyCard )
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
                                .single()
                                .flatMap( person -> Mono.zip(
                                        this.getGetModelForAddress().apply( person.getPCitizen() ),
                                        this.getGetModelForPassport().apply( person.getPPsp(), person.getPDateBirth() ) ) )
                                .map( tuple1 -> {
                                    psychologyCard.setModelForPassport( tuple1.getT2() );
                                    psychologyCard.setModelForAddress( tuple1.getT1() );
                                    return psychologyCard; } )
                                : Mono.just( psychologyCard ); } )
                    : Mono.just( psychologyCard );

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

//                if ( this.getCheckFamily().test( psychologyCard.getChildData() ) ) psychologyCard
//                        .getChildData()
//                        .getItems()
//                        .parallelStream()
//                        .forEach( familyMember -> this.getGetImageByPinfl()
//                                .apply( familyMember.getPnfl() )
//                                .subscribe( familyMember::setPersonal_image ) );
//
//                if ( this.getCheckFamily().test( psychologyCard.getDaddyData() ) ) psychologyCard
//                        .getDaddyData()
//                        .getItems()
//                        .parallelStream()
//                        .forEach( familyMember -> this.getGetImageByPinfl()
//                                .apply( familyMember.getPnfl() )
//                                .subscribe( familyMember::setPersonal_image ) );
//
//                if ( this.getCheckFamily().test( psychologyCard.getMommyData() ) ) psychologyCard
//                        .getMommyData()
//                        .getItems()
//                        .parallelStream()
//                        .forEach( familyMember -> this.getGetImageByPinfl()
//                                .apply( familyMember.getPnfl() )
//                                .subscribe( familyMember::setPersonal_image ) );
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
                        return Mono.zip( this.getFindAllDataAboutCar().apply( psychologyCard ),
                                        this.getSetPersonPrivateDataAsync().apply( psychologyCard ),
                                        this.getFindAllAboutFamily().apply( tuple.getT5(), psychologyCard ) )
                                .map( tuple1 -> {
                                    this.getSaveUserUsageLog().accept( new UserRequest( psychologyCard, apiResponseModel ) );
                                    return tuple1.getT1(); } ); } )
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
                    .flatMap( psychologyCard -> Mono.zip(
                                    this.getFindAllDataAboutCar().apply( psychologyCard ),
                                    this.getFindAllAboutFamily().apply( results, psychologyCard ),
                                    this.getSetPersonPrivateDataAsync().apply( psychologyCard ) )
                            .map( tuple1 -> {
                                this.getSaveUserUsageLog().accept( new UserRequest( psychologyCard, apiResponseModel ) );
                                return tuple1.getT1(); } ) );

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
                return Mono.zip(
                                this.getSetPersonPrivateDataAsync().apply( psychologyCard ),
                                this.getFindAllDataAboutCar().apply( psychologyCard ),
                                this.getFindAllAboutFamily().apply( tuple.getT6(), psychologyCard ) )
                        .map( tuple1 -> {
                            this.getSaveUserUsageLog().accept( new UserRequest( psychologyCard, apiResponseModel ) );
                            return tuple1.getT1(); } ); } )
            : Mono.just( new PsychologyCard( this.getGetDataNotFoundErrorResponse().apply( Errors.DATA_NOT_FOUND.name() ) ) );

    private final Function< String, String > test = base64 -> {
        this.getHeaders().put( "Content-Type", "application/json" );
        this.getHeaders().put( "Authorization Bearer ", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQTA5NTgwMzMiLCJpZCI6IjUxZWU0ZWVlLTRhZmQtNDUzMy1iNGZiLWU3ZWNhZDA2ODM2NiIsInBhc3Nwb3J0TnVtYmVyIjoiQUEwOTU4MDMzIiwiZnVsbG5hbWUiOiJTYWR1bGxheWV2IE11cm9kdWxsYSBVYmF5ZHVsbGEgb-KAmGfigJhsIiwicGhvbmVOdW1iZXIiOiIrKDk5OCk5NyA3MzMtMjUtNTUiLCJyb2xlIjoiUk9MRV9TVVBFUl9BRE1JTiIsInJlZ2lvbiI6IlRvc2hrZW50IHZpbG95YXQiLCJkZXBhcnRtZW50IjoiUGF0cnVsLXBvc3QgeGl6bWF0aSB2YSBqYW1vYXQgdGFydGliaW5pIHNhcWxhc2ggYm9zaCBib3NocWFybWFzaSIsIm1pbGl0YXJ5UmFuayI6IkthdHRhIGxleXRlbmFudCIsInBvc2l0aW9uIjoiSXQgbXV0YXh4YXNpcyIsInVzZXJQaG90b1VybCI6Imh0dHBzOi8vbXMtZGV2LnNzZC51ei9maWxlcy9hcGkvdjEvaW1hZ2UvYWRtaW5fcGFuZWwvMjAyMi0xMS8wMWQvMDZoLzY4MGM5MDZmLWIyMDUtNGFkYS04NjQ0LWU0MDMyMmEzN2Q2MS5qcGciLCJwZXJtaXNzaW9ucyI6WyJCT1pPUkxBUiIsIkhBTU1PTV9CSU5PU0kiLCJTSEFSVExJX0hVS01fUUlMSU5HQU5MQVIiLCJTQVlJTEdPSExBUiIsIkVMT05fQkVSSUxHQU5MQVJfUUlESVJVVkkiLCLQoUhFVC1FTF9GVVFBUk9MQVJJIiwiSklTTU9OSVlfVkFfWVVSSURJS19TSEFYU0xBUk5JTkdfQVZUT01PVE9UUkFOU1BPUlRJIiwiSUpUSU1PSVlfVEFSQU5HTElLIiwiWUFOR0lfUUlESVJVViIsIklCT0RBVFhPTkFMQVIiLCJTSE9TSElMSU5DSF9YQUJBUkxBUiIsIk1BWFNVU19BVlRPVFJBTlNQT1JUX1ZPU0lUQUxBUkkiLCJNQcq8TVVSSVlfTkFaT1JBVF9UQcq8U0lSSV9PU1RJR0FfVFVTSFVWQ0hJTEFSIiwiUlVISVlfS0FTQUxMQVIiLCJEQVZMQVRMQVJBUk9fQVZUT1FJRElSVVYiLCJDQU1FUkFfR1JPVVAiLCJGQVJaQU5EX1RBUkJJWUFTSUdBX1NBTEJJWV9UQcq8U0lSX0tPyrxSU0FUVVZDSElfT1RBLU9OQSIsIlRFU1QxIiwiQ0hFVF9FTF9GVVFBUk9MQVJJIiwiVEVTVDIiLCJFUktJTl9UVVJJU1RJS19aT05BIiwiR0VPWEFSSVRBIiwiTUFKQlVSSVlfSkFNT0FUX0lTSExBUkkiLCJKQVpPTklfT8q8VEFTSERBTl9NVUREQVRJREFOX0lMR0FSSV9TSEFSVExJX09aT0RfUUlMSU5HQU5MQVIiLCJUVVJBUl9PQllFS1RMQSIsIk1ByrxNVVJJWV9OQVpPUkFUREFfVFVSR0FOTEFSIiwiS0FUT0xJS19JQk9EQVRYT05BTEFSSSIsIkRBVkxBVExBUkFST19RSURJUlVWREFHSV9TSEFYU0xBUiIsIk_KvFRBX1hBVkZMSV9SRVRTSURJVklTVCIsIlZPS1pBTCIsItCQVlRPTU9CSUxfUUlESVJVVkkiLCJHUFNfTkFaT1JBVEkiLCJOT1FPUU5VTklZX1RP4oCZWFRBU0giLCLQkFZJQV9PTkxBWU4iLCLQkFZUT19JREVOVElGSUtBVFNJWUEiLCJQUk9GSUxBS1RJS19YSVNPQkxBUiIsIkVfQlJBU0xFVCIsIkpUU0IiLCJKSUVEIiwiUUlESVJVVl9FyrxMT05fUUlMSVNIIiwi0JBOVElLVkFSSUFUIiwiU01BUlRfTUFIQUxMIiwiWklZT1JBVEdPWCIsIkdJWU9IVkFORExBUiIsIklCRCIsIk1ByrxNVVJJWV9YVVFVUUJVWkFSTElLTEFSIiwiUExZQUoiLCJNVVFBRERBTV9TVURMQU5HQU5fU0hBWFNMQVIiLCJPWk9ETElLTklfQ0hFS0xBU0giLCJZT8q8UU9USUxHQU5fSEFZRE9WQ0hJTElLX0dVVk9ITk9NQUxBUkkiLCJQUk9CQVRTSVlBX1hJU09CSSIsIk1BU0pJRCIsIlBST0ZJTEFLVElLLUhJU09CTEFSIiwiU1BJUlRMSV9JQ0hJTUxJS0tBX1JVSlVfUU_KvFlHQU5MQVIiLCJUUkFOU1BPUlRfT0JZRUtUTEFSSSIsIlNIQUhBUl9LVVpBVFVWSSIsIlBUWl9DT05UUk9MIiwiVFVSQVJfVkFfTk9UVVJBUl9PQllFS1RMQVIiLCJLTydOR0lMT0NIQVJfU0FWRE9fTUFSS0FaTEFSSSIsIk1ByrxNVVJJWV9IVVFVUUJVWkFSTElLX1NPRElSX1FJTEdBTl9TSEFYU0xBUiIsIkUtTUVITU9OX1BMQVRGT1JNQVNJIiwiVEVMRU1JTk9SQSIsIktP4oCZUF9RQVZBVExJX1VZTEFSIiwiTURIX0RBVkxBVExBUklEQV9ZT8q8UU9USUxHQU5fVkFfQU5JUUxBTkdBTl9RVVJPTExBUiIsIlBST0JBVFNJWUEtSElTT0JJIiwiT0JTRVJWQVRPUklZQSIsItCQTklRTEFOR0FOX1FJRElSVVZEQUdJTEFSIiwiREYiLCJRT-KAmU5H4oCZSVJPUUxBUl9UQVFTSU1PVEkiLCJERU1PR1JBRklLX1hJU09CIiwiS08nQ0hBTEFSIiwiTVVaRVkiLCJJWFRJWE9TTEFTSFRJUklMR0FOX0_KvFFVVi1UQVJCSVlBX01VQVNTQVNBU0lEQU5fUUFZVEdBTkxBUiIsIlRBTklCX09MSVNIIiwiQ0hJUFRBX1hJU09CSSIsIlBPU1RfUEFUUlVMTEFSSSIsIkRJUVFBVEdBX1NBWk9WT1JfSk9ZTEFSIiwiS1VaQVRVVl9LQU1FUkFMQVIiLCJLTydQX1FBVkFUTElfVVlMQVIiLCJDSE9SUkFIQUxBUiIsIk5PVFVSQVJfT0JZRUtUTEFSIiwiSFVEVURMQVJOSV9OQVpPUkFUX1FJTElTSCIsIkFWVE9NT0JJTF9ZTydMTEFSSSIsIlFJU0hMT1FMQVIiLCJEQUNIQUxBUiIsIktBRkVMQVIiLCLQkFhMT1FfVFVaQVRJU0hfSVNITEFSSSIsIlNIQVhTX0lERU5USUZJS0FUU0lZQVNJIiwiU0hBTlhBWV9IQU1LT1JMSUtfVEFTSEtJTE9USSIsIllUWCIsIlhVRFVEX05BWk9SQVRJIiwiWU_KvFFPVElMR0FOX1BBU1BPUlRMQVIiLCJURVpLT1ItTUHKvExVTU9UTEFSX0hJU09CSSIsIkpJTk9JWV9JU0hMQVJfWElTT0JJIiwiTUVUUk8iLCJST0xFX1NVUEVSX0FETUlOIiwiQU5JUUxBTkdBTkxBUl9RSURJUlVWSSIsIk1BUktFVExBUiIsIlRFQVRSIiwiTUFIQUxMSVlfUUlESVJVVkRBR0lfU0hBWFNMQVIiLCJCVVlVTSIsIlRFU1QiLCJHRU9fWEFSSVRBIiwiS08nUkdBWk1BX1pBTExBUkkiLCJQUk9WQVNMQVZfQ0hFUktPVkkiLCJTSEFYU19RSURJUlVWSSIsIk1JTk9SQSIsIlFVUk9MX0hJU09CSSIsIk9JTEEtVFVSTVVTSF9NVU5PU0FCQVRMQVJJX0RPSVJBU0lEQV9IVVFVUUJVWkFSTElLX1NPRElSX0VUR0FOTEFSIiwiT1JPTUdPSCIsIlZPS1pBTFMiLCJQU0lYT0xPR0lLX0tBUlRBIiwiS1VDSF9WQV9WT1NJVEFMQVIiLCJURU1JUl9ZT8q8TF9EQVNIQk9SRCIsIkZVUUFST1ZJWV9WQV9YSVpNQVRfUVVST0xJIiwiVFJBTlNQT1JUX1ZBX1RVUklaTV9LT-KAmVJTQVRLSUNITEFSSSIsIktJTk9URUFUUiIsIllPyrxMX1RSQU5TUE9SVF9YT0RJU0FMUkkiLCJFX01BTVVSSVkiLCJWT1lBR0FfWUVUTUFHQU5MQVIiLCJKSU5PSVlfSFVRVVFJWV9TVEFUSVNUSUtBIiwiU0hBUlNIQVJBTEFSIiwiWU9ER09STElLTEFSIiwiU1VOSVlfSU5URUxMRUtUIiwiSklOT1lBVExBUiIsIlFP4oCZTkfigJlJUk9RTEFSX1hJWk1BVEkiLCLQkEVST1BPUlQiLCIxMDIiLCJKSU5PSVlfSVNIX01JTExJWV9HVkFSRElZQSIsIllPyrxRT1RJTEdBTl9URVhQQVNQT1JUTEFSIiwiSE9WTEkiLCJNVUFZWUFOX0hVUVVRREFOX01BSFJVTV9RSUxJTkdBTkxBUiIsIlhBVkZTSVpfVFJBTlNQT1JUIiwiVFVSSVpNX09CWUVLVExBUiIsIklKVElNT0lZX1hPTEFUIl0sImlhdCI6MTY3NDQ1NTU5NywiZXhwIjoxNjc0NTQxOTk3fQ.mZ_m-L2neM4Y1dy3WWlC2gTV7_5L-AGVKVBUg3DaVz0tiQoP8MSGSAnAOTi2kHSogoGdLXzwbFw4YSa4S7fDdw" );
        HttpResponse< JsonNode > response;
        try {
            response = Unirest.get( base64 ).asJson();
            log.info( "Response for: {}, {}", base64, response.getStatus() );
            if ( response.getBody().toString().length() < 500 ) log.error( response.getBody().toString() );
            return response.getBody().toString(); }
        catch ( UnirestException e ) {
            log.error( "Error: " + e.getMessage() );
            return Errors.SERVICE_WORK_ERROR.name(); } };

    @Override
    public void run () {
        while ( serDes != null ) {
            this.updateTokens();
            try { TimeUnit.HOURS.sleep( 3 ); } catch ( InterruptedException e ) { e.printStackTrace(); } } }
}