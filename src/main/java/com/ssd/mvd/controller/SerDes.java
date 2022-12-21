package com.ssd.mvd.controller;

import java.util.*;
import java.util.function.*;
import java.util.concurrent.TimeUnit;

import reactor.netty.ByteBufFlux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.ssd.mvd.entity.*;
import com.ssd.mvd.constants.Errors;
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
    private final HttpClient httpClient = HttpClient.create();
    private final Notification notification = new Notification();

    private final Map< String, Object > fields = new HashMap<>();
    private final Map< String, String > headers = new HashMap<>();

    public static SerDes getSerDes () { return serDes != null ? serDes : ( serDes = new SerDes() ); }

    private <T> List<T> stringToArrayList ( String object, Class< T[] > clazz ) { return Arrays.asList( this.getGson().fromJson( object, clazz ) ); }

    private SerDes () { Unirest.setObjectMapper( new ObjectMapper() {
        private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

        @Override
        public String writeValue( Object o ) {
            try { return this.objectMapper.writeValueAsString( o ); }
            catch ( JsonProcessingException e ) { throw new RuntimeException(e); } }

        @Override
        public <T> T readValue( String s, Class<T> aClass ) {
            try { return this.objectMapper.readValue( s, aClass ); }
            catch ( JsonProcessingException e ) { throw new RuntimeException(e); } } } ); }

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
            this.setTokenForFio(
                    String.valueOf( Unirest.post( this.getConfig().getAPI_FOR_FIO_TOKEN() )
                            .header("Content-Type", "application/json" )
                            .body("{\r\n    \"Login\": \"" + this.getConfig().getLOGIN_FOR_FIO_TOKEN()
                                    + "\",\r\n    \"Password\": \"" + this.getConfig().getPASSWORD_FOR_FIO_TOKEN()
                                    + "\",\r\n    \"CurrentSystem\": \"" + this.getConfig().getCURRENT_SYSTEM_FOR_FIO() + "\"\r\n}")
                            .asJson()
                            .getBody()
                            .getObject()
                            .get( "access_token" ) ) );
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
            .message( "Service error: " + error )
            .errors( Errors.EXTERNAL_SERVICE_500_ERROR)
            .build();

    private final Function< String, ErrorResponse > getServiceErrorResponse = error -> ErrorResponse
            .builder()
            .message( "Service error: " + error )
            .errors( Errors.SERVICE_WORK_ERROR )
            .build();

    private final Function< String, ErrorResponse > getDataNotFoundErrorResponse = error -> ErrorResponse
            .builder()
            .message( "Data for: " + error + " not found" )
            .errors( Errors.DATA_NOT_FOUND )
            .build();

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

    private final Consumer< UserRequest > saveUserUsageLog = userRequest -> Mono.just( userRequest )
            .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                    error.getMessage(), object ) ) )
            .subscribe( userRequest1 -> KafkaDataControl
                    .getInstance()
                    .getWriteToKafkaServiceUsage()
                    .accept( this.getGson().toJson( userRequest1 ) ) );

    private final Function< String, Mono< String > > base64ToLink = base64 -> this.getHttpClient()
            .headers( h -> h.add( "Content-Type", "application/json" ) )
            .post()
            .send( ByteBufFlux.fromString( Mono.just( "{\r\n    \"serviceName\" : \"psychologyCard\",\r\n    \"photo\" : \"" + base64 + "\"\r\n}" ) ) )
            .uri( this.getConfig().getBASE64_IMAGE_TO_LINK_CONVERTER_API() )
            .responseSingle( ( ( res, content ) -> res.status().code() == 200
                    && content != null
                    ? content
                    .asString()
                    .map( s -> s.substring( s.indexOf( "data" ) + 7, s.length() - 2 ) )
                    : Mono.just( Errors.DATA_NOT_FOUND.name() ) ) )
            .doOnError( throwable -> log.error( "Error: " + throwable.getMessage() ) )
            .doOnSuccess( value -> log.info( "Success: " + value ) );

    private final Function< String, Mono< Pinpp > > getPinpp = pinpp -> this.getHttpClient()
            .headers( h -> {
                h.clear();
                h.add( "Authorization", "Bearer " + this.getTokenForPassport() ); } )
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
                        : Mono.just( new Pinpp(
                        this.getDataNotFoundErrorResponse.apply( pinpp ) ) ); } )
            .doOnError( e -> {
                log.error( "Error in pinpp method: {}", e.getMessage() );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.OVIR.getName(),
                        IntegratedServiceApis.OVIR.getDescription() );
                this.sendErrorLog( "pinpp", pinpp, "Error in service: " + e.getMessage() ); } )
            .onErrorReturn( new Pinpp( this.getServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< Data > > deserialize = pinfl -> this.getHttpClient()
            .headers( h -> {
                h.clear();
                h.add( "Authorization", "Bearer " + this.getTokenForPassport() ); } )
            .post()
            .send( ByteBufFlux.fromString( Mono.just( "{\r\n    \"Pcadastre\": \"" + pinfl + "\"\r\n}" ) ) )
            .uri( this.getConfig().getAPI_FOR_CADASTR() )
            .responseSingle( ( res, content ) -> {
                log.info( "Pcadastre in deserialize: " + pinfl );
                if ( res.status().code() == 401 ) {
                    this.updateTokens();
                    return this.getDeserialize().apply( pinfl ); }

                if ( this.check500ErrorAsync.test( res.status().code() ) ) {
                    this.saveErrorLog(
                            res.status().toString(),
                            IntegratedServiceApis.OVIR.getName(),
                            IntegratedServiceApis.OVIR.getDescription() );
                    return Mono.just( new Data(
                            this.getExternalServiceErrorResponse.apply( res.status().toString() ) ) ); }

                return content != null
                        && res.status().code() == 200
                        ? content
                        .asString()
                        .map( s -> {
                            String temp = s.substring( s.indexOf( "Data" ) + 7, s.length() - 2 );
                            log.info( "Temp Response: " + temp );
                            return this.getGson().fromJson( temp, Data.class ); } )
                        : Mono.just( new Data( this.getDataNotFoundErrorResponse.apply( pinfl ) ) ); } )
            .doOnError( e -> {
                log.error( "Error in deserialize of Cadastre method: {}", e.getMessage() );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.OVIR.getName(),
                        IntegratedServiceApis.OVIR.getDescription() );
                this.sendErrorLog( "deserialize ModelForCadastr", pinfl, "Error: " + e.getMessage() ); } )
            .onErrorReturn( new Data( this.getServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< String > > getImageByPinfl = pinfl -> this.getHttpClient()
            .headers( h -> {
                h.clear();
                h.add( "Authorization", "Bearer " + this.getTokenForGai() ); } )
            .get()
            .uri( this.getConfig().getAPI_FOR_PERSON_IMAGE() + pinfl )
            .responseSingle( ( res, content ) -> {
                log.info( "Pinfl in getImageByPinfl: " + pinfl );
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
                        .map( s -> s.substring( s.indexOf( "Data" ) + 7, s.length() - 2 ) )
                        : Mono.just( Errors.DATA_NOT_FOUND.name() ); } )
            .doOnError( e -> {
                log.error( "Error in deserialize of Cadastre method: {}", e.getMessage() );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.OVIR.getName(),
                        IntegratedServiceApis.OVIR.getDescription() );
                this.sendErrorLog( "getImageByPinfl", pinfl, "Error: " + e.getMessage() ); } )
            .onErrorReturn( Errors.DATA_NOT_FOUND.name() );

    private final Function< String, Mono< ModelForAddress > > getModelForAddress = pinfl -> this.getHttpClient()
            .headers( h -> {
                h.clear();
                h.add( "Authorization", "Bearer " + this.getTokenForGai() ); } )
            .post()
            .uri( this.getConfig().getAPI_FOR_MODEL_FOR_ADDRESS() )
            .send( ByteBufFlux.fromString( Mono.just( "{\r\n    \"Pcitizen\":\"" + pinfl + "\"\r\n}" ) ) )
            .responseSingle( ( res, content ) -> {
                log.info( "Pinfl in getModelForAddress: " + pinfl );
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
                        .map( s -> this.getGson()
                                .fromJson( s.substring( s.indexOf( "Data" ) + 7, s.length() - 2 ),
                                        ModelForAddress.class ) )
                        : Mono.just( new ModelForAddress( this.getDataNotFoundErrorResponse.apply( pinfl ) ) ); } )
            .doOnError( e -> {
                log.error( "Error in getModelForAddress method: {}", e.getMessage() );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.OVIR.getName(),
                        IntegratedServiceApis.OVIR.getDescription() );
                this.sendErrorLog( "getModelForAddress", pinfl, "Error: " + e.getMessage() ); } )
            .onErrorReturn( new ModelForAddress(
                    this.getServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final BiFunction< String, String, Mono< com.ssd.mvd.entity.modelForPassport.Data > > getModelForPassport =
            ( SerialNumber, BirthDate ) -> this.getHttpClient()
                    .headers( h -> {
                        h.clear();
                        h.add( "Authorization", "Bearer " + this.getTokenForPassport() ); } )
                    .post()
                    .uri( this.getConfig().getAPI_FOR_PASSPORT_MODEL() )
                    .send( ByteBufFlux.fromString( Mono.just( "{\r\n    " +
                            "\"SerialNumber\":\"" + SerialNumber + "\",\r\n    " +
                            "\"BirthDate\":\"" + BirthDate + "\"\r\n}" ) ) )
                    .responseSingle( ( res, content) -> {
                        if ( res.status().code() == 401 ) {
                            this.updateTokens();
                            return this.getGetModelForPassport().apply( SerialNumber, BirthDate ); }

                        if ( this.check500ErrorAsync.test( res.status().code() ) ) {
                            this.saveErrorLog(
                                    res.status().toString(),
                                    IntegratedServiceApis.OVIR.getName(),
                                    IntegratedServiceApis.OVIR.getDescription() );
                            return Mono.just( new com.ssd.mvd.entity.modelForPassport.Data(
                                    this.getExternalServiceErrorResponse.apply( res.status().toString() ) ) ); }

                        return res.status().code() == 200
                                && content != null
                                ? content
                                .asString()
                                .map( s -> this.getGson()
                                        .fromJson( s.substring( s.indexOf( "Data" ) + 7, s.length() - 2 ),
                                                com.ssd.mvd.entity.modelForPassport.Data.class ) )
                                : Mono.just( new com.ssd.mvd.entity.modelForPassport.Data(
                                this.getDataNotFoundErrorResponse.apply(
                                        SerialNumber + " : " + SerialNumber ) ) ); } )
                    .doOnError( e -> {
                        log.error( "Error in getModelForPassport method: {}", e.getMessage() );
                        this.saveErrorLog( e.getMessage(),
                                IntegratedServiceApis.OVIR.getName(),
                                IntegratedServiceApis.OVIR.getDescription() );
                        this.sendErrorLog( "deserialize Passport Data",
                                SerialNumber + "_" + BirthDate,
                                "Error: " + e.getMessage() ); } )
                    .onErrorReturn( new com.ssd.mvd.entity.modelForPassport.Data(
                            this.getServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< Insurance > > insurance = gosno -> this.getHttpClient()
            .headers( h -> {
                h.clear();
                h.add( "Authorization", "Bearer " + this.getTokenForGai() ); } )
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
                        .map( s -> {
                            log.info( "Body: " + s );
                            log.info( "Object: " + this.getGson().fromJson( s, Insurance.class ) );
                            return !s.contains( "топилмади" )
                                    ? this.getGson().fromJson( s, Insurance.class )
                                    : new Insurance( this.getServiceErrorResponse.apply( Errors.DATA_NOT_FOUND.name() ) ); } )
                        : Mono.just( new Insurance(
                        this.getDataNotFoundErrorResponse.apply( gosno ) ) ); } )
            .doOnError( e -> {
                log.error( "Error in insurance method: {}", e.getMessage() );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.GAI.getName(),
                        IntegratedServiceApis.GAI.getDescription() );
                this.sendErrorLog( "insurance", gosno, "Error: " + e.getMessage() ); } )
            .onErrorReturn( new Insurance( this.getServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< ModelForCar > > getVehicleData = gosno -> this.getHttpClient()
            .headers( h -> {
                h.clear();
                h.add( "Authorization", "Bearer " + this.getTokenForGai() ); } )
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
                        .map( s -> {
                            log.info( "Body: " + s );
                            log.info( "Object: " + this.getGson().fromJson( s, ModelForCar.class ) );
                            return this.getGson().fromJson( s, ModelForCar.class ); } )
                        : Mono.just( new ModelForCar(
                        this.getDataNotFoundErrorResponse.apply( gosno ) ) ); } )
            .doOnError( e -> {
                log.error( "Error in getVehicleData method: {}", e.getMessage() );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.GAI.getName(),
                        IntegratedServiceApis.GAI.getDescription() );
                this.sendErrorLog( "getVehicleData", gosno, e.getMessage() ); } )
            .onErrorReturn( new ModelForCar( this.getServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< Tonirovka > > getVehicleTonirovka = gosno -> this.getHttpClient()
            .headers( h -> {
                h.clear();
                h.add( "Authorization", "Bearer " + this.getTokenForGai() ); } )
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
                        .map( s -> {
                            log.info( "Body: " + s );
                            log.info( "Object: " + this.getGson().fromJson( s, Tonirovka.class ) );
                            return this.getGson().fromJson( s, Tonirovka.class ); } )
                        : Mono.just( new Tonirovka(
                        this.getDataNotFoundErrorResponse.apply( gosno ) ) ); } )
            .doOnError( e -> {
                log.error( "Error in getVehicleTonirovka method: {}", e.getMessage() );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.GAI.getName(),
                        IntegratedServiceApis.GAI.getDescription() );
                this.sendErrorLog( "getVehicleTonirovka", gosno, e.getMessage() ); } )
            .onErrorReturn( new Tonirovka( this.getServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< ViolationsList > > getViolationList = gosno -> this.getHttpClient()
            .headers( h -> {
                h.clear();
                h.add( "Authorization", "Bearer " + this.getTokenForGai() ); } )
            .get()
            .uri( this.getConfig().getAPI_FOR_VIOLATION_LIST() + gosno )
            .responseSingle( ( res, content ) -> {
                log.info( "Gosno in getViolationList: " + gosno
                        + " With status: " + res.status() );
                if ( res.status().code() == 401 ) {
                    this.updateTokens();
                    return this.getViolationList.apply( gosno ); }

                if ( this.check500ErrorAsync.test( res.status().code() ) ) {
                    this.saveErrorLog(
                            res.status().toString(),
                            IntegratedServiceApis.GAI.getName(),
                            IntegratedServiceApis.GAI.getDescription() );
                    return Mono.just( new ViolationsList(
                            this.getExternalServiceErrorResponse.apply( res.status().toString() ) ) ); }

                return res.status().code() == 200
                        && content != null
                        ? content
                        .asString()
                        .map( s -> {
                            log.info( "Body: " + s );
                            log.info( "Object: " + new ViolationsList(
                                    this.stringToArrayList( s, ViolationsInformation[].class ) ) );
                            return new ViolationsList(
                                    this.stringToArrayList( s, ViolationsInformation[].class ) ); })
                        : Mono.just( new ViolationsList(
                        this.getDataNotFoundErrorResponse.apply( gosno ) ) ); } )
            .doOnError( e -> {
                log.error( "Error in getViolationList method: {}", e.getMessage() );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.GAI.getName(),
                        IntegratedServiceApis.GAI.getDescription() );
                this.sendErrorLog( "getViolationList", gosno, e.getMessage() ); } )
            .onErrorReturn( new ViolationsList(
                    this.getServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    private final Function< String, Mono< DoverennostList > > getDoverennostList = gosno -> this.getHttpClient()
            .headers( h -> {
                h.clear();
                h.add( "Authorization", "Bearer " + this.getTokenForGai() ); } )
            .get()
            .uri( this.getConfig().getAPI_FOR_DOVERENNOST_LIST() + gosno )
            .responseSingle( ( res, content ) -> {
                log.error( "Gosno in getDoverennostList: " + this.getConfig().getAPI_FOR_DOVERENNOST_LIST() + gosno
                        + " With status: " + res.status() );
                if ( res.status().code() == 401 ) {
                    this.updateTokens();
                    return this.getDoverennostList.apply( gosno ); }

                if ( this.check500ErrorAsync.test( res.status().code() ) ) {
                    this.saveErrorLog(
                            res.status().toString(),
                            IntegratedServiceApis.GAI.getName(),
                            IntegratedServiceApis.GAI.getDescription() );
                    return Mono.just( new DoverennostList(
                            this.getExternalServiceErrorResponse.apply( res.status().toString() ) ) ); }

                return res.status().code() == 200
                        && content != null
                        ? content
                        .asString()
                        .map( s -> {
                            log.info( "Body: " + s );
                            log.info( "Object: " + new DoverennostList(
                                    this.stringToArrayList( s, Doverennost[].class ) ) );
                            return new DoverennostList(
                                    this.stringToArrayList( s, Doverennost[].class ) ); } )
                        : Mono.just( new DoverennostList(
                        this.getDataNotFoundErrorResponse.apply( Errors.DATA_NOT_FOUND.name() ) ) ); } )
            .doOnError( e -> {
                log.error( "Error in getDoverennostList method: {}", e.getMessage() );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.GAI.getName(),
                        IntegratedServiceApis.GAI.getDescription() );
                this.sendErrorLog( "getDoverennostList", gosno, "Error: " + e.getMessage() ); } )
            .onErrorReturn( new DoverennostList( this.getServiceErrorResponse.apply( gosno ) ) );

    private final Function< String, Mono< ModelForCarList > > getModelForCarList = pinfl -> this.getHttpClient()
            .headers( h -> {
                h.clear();
                h.add( "Authorization", "Bearer " + this.getTokenForGai() ); } )
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
                            this.getExternalServiceErrorResponse.apply( res.status().toString() ) ) ); }

                return res.status().code() == 200
                        && content != null
                        ? content
                        .asString()
                        .map( s -> new ModelForCarList(
                                this.stringToArrayList( s, ModelForCar[].class ) ) )
                        : Mono.just( new ModelForCarList(
                        this.getDataNotFoundErrorResponse.apply( Errors.DATA_NOT_FOUND.name() ) ) ); } )
            .doOnError( e -> {
                log.error( "Error in getModelForCarList method: {}", e.getMessage() );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.GAI.getName(),
                        IntegratedServiceApis.GAI.getDescription() );
                this.sendErrorLog( "getModelForCarList", pinfl, "Error: " + e.getMessage() ); } )
            .onErrorReturn( new ModelForCarList(
                    this.getServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

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

    private final Consumer< PsychologyCard > findAllDataAboutCar = psychologyCard -> {
        if ( this.checkCarData.test( psychologyCard ) ) psychologyCard
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

    private final Consumer< PsychologyCard > setPersonPrivateData = psychologyCard -> this.getDeserialize()
            .apply( psychologyCard.getPinpp().getCadastre() )
            .subscribe( data -> {
                psychologyCard.setModelForCadastr( data );
                if ( this.checkPrivateData.test( psychologyCard ) ) psychologyCard
                        .getModelForCadastr()
                        .getPermanentRegistration()
                        .parallelStream()
                        .filter( person -> person
                                .getPDateBirth()
                                .equals( psychologyCard
                                        .getPinpp()
                                        .getBirthDate() ) )
                        .forEach( person -> {
                            this.getGetModelForPassport().apply( person.getPPsp(), person.getPDateBirth() )
                                    .subscribe( psychologyCard::setModelForPassport );
                            this.getGetModelForAddress().apply( person.getPCitizen() )
                                    .subscribe( psychologyCard::setModelForAddress ); } ); } );

    private final Predicate< Family > checkFamily = family ->
            family != null
            && family.getItems() != null
            && !family.getItems().isEmpty();

    private void setFamilyData ( Results results, PsychologyCard psychologyCard ) {
        // личные данные человека чьи данные были переданы на данный сервис
        psychologyCard.setChildData( results.getChildData() );

        // личные данные матери, того чьи данные были переданы на данный сервис
        psychologyCard.setMommyData( results.getMommyData() );
        psychologyCard.setMommyPinfl( results.getMommyPinfl() );

        // личные данные отца, того чьи данные были переданы на данный сервис
        psychologyCard.setDaddyData( results.getDaddyData() );
        psychologyCard.setDaddyPinfl( results.getDaddyPinfl() );

        if ( this.checkFamily.test( psychologyCard.getChildData() ) ) psychologyCard
                .getChildData()
                .getItems()
                .parallelStream()
                .forEach( familyMember -> this.getGetImageByPinfl()
                        .apply( familyMember.getPnfl() )
                        .subscribe( familyMember::setPersonal_image ) );

        if ( this.checkFamily.test( psychologyCard.getDaddyData() ) ) psychologyCard
                .getDaddyData()
                .getItems()
                .parallelStream()
                .forEach( familyMember -> this.getGetImageByPinfl()
                        .apply( familyMember.getPnfl() )
                        .subscribe( familyMember::setPersonal_image ) );

        if ( this.checkFamily.test( psychologyCard.getMommyData() ) ) psychologyCard
                .getMommyData()
                .getItems()
                .parallelStream()
                .forEach( familyMember -> this.getGetImageByPinfl()
                        .apply( familyMember.getPnfl() )
                        .subscribe( familyMember::setPersonal_image ) ); }

    private final Function< FIO, Mono< PersonTotalDataByFIO > > getPersonTotalDataByFIO = fio -> this.getHttpClient()
            .headers( h -> {
                h.clear();
                h.add( "Authorization", "Bearer " + this.getTokenForFio() ); } )
            .post()
            .uri( this.getConfig().getAPI_FOR_PERSON_DATA_FROM_ZAKS() )
            .send( ByteBufFlux.fromString( Mono.just(
                    "{\r\n    \"surname\" : \"ERGASHOVA\",\r\n    \"name\" : \"ZILOLA\",\r\n    \"patronym\" : \"ABDUMUTALOVNA\"\r\n}" ) ) )
            .responseContent()
            .asString()
            .next()
            .map( s -> {
                log.info( "Response: " + s );
                PersonTotalDataByFIO person = this.getGson()
                        .fromJson( s, PersonTotalDataByFIO.class );
                if ( person != null
                        && person.getData() != null
                        && person.getData().size() > 0 ) {
                    person
                            .getData()
                            .parallelStream()
                            .forEach( person1 -> this.getGetImageByPinfl()
                                    .apply( person1.getPinpp() )
                                    .subscribe( person1::setPersonImage ) );
                    this.getSaveUserUsageLog().accept( new UserRequest( person, fio ) ); }
                return person != null ? person
                        : new PersonTotalDataByFIO(
                        this.getServiceErrorResponse.apply(
                                Errors.DATA_NOT_FOUND.name() ) ); } )
            .doOnError( e -> {
                log.error( "Error in getPersonTotalDataByFIO method: {}", e.getMessage() );
                this.saveErrorLog( e.getMessage(),
                        IntegratedServiceApis.GAI.getName(),
                        IntegratedServiceApis.GAI.getDescription() );
                this.sendErrorLog( "getPersonTotalDataByFIO", fio.getName(), "Error: " + e.getMessage() ); } )
            .onErrorReturn( new PersonTotalDataByFIO(
                    this.getServiceErrorResponse.apply( Errors.SERVICE_WORK_ERROR.name() ) ) );

    public PsychologyCard getPsychologyCard ( ApiResponseModel apiResponseModel ) {
        if ( apiResponseModel.getStatus().getMessage() == null ) return null;
        PsychologyCard psychologyCard = new PsychologyCard();
        FindFaceComponent
                .getInstance()
                .getViolationListByPinfl( apiResponseModel.getStatus().getMessage() )
                .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ", error.getMessage(), object ) ) )
                .onErrorReturn( new ArrayList() )
                .subscribe( list -> psychologyCard.setViolationList( list != null ? list : new ArrayList<>() ) );

        log.info( "Pinfl before: " + apiResponseModel.getStatus().getMessage() );
        FindFaceComponent
                .getInstance()
                .getFamilyMembersData( apiResponseModel.getStatus().getMessage() )
                .subscribe( results -> this.setFamilyData( results, psychologyCard ) );

        this.getGetPinpp().apply( apiResponseModel.getStatus().getMessage() )
                .subscribe( pinpp -> {
                    psychologyCard.setPinpp( pinpp );
                    this.getSetPersonPrivateData().accept( psychologyCard ); } );
        this.getGetModelForCarList().apply( apiResponseModel.getStatus().getMessage() )
                .subscribe( modelForCarList -> {
                    psychologyCard.setModelForCarList( modelForCarList );
                    this.getFindAllDataAboutCar().accept( psychologyCard ); } );
        this.getGetImageByPinfl().apply( apiResponseModel.getStatus().getMessage() )
                .subscribe( psychologyCard::setPersonImage );
        this.getSaveUserUsageLog().accept( new UserRequest( psychologyCard, apiResponseModel ) );
        return psychologyCard; }

    public PsychologyCard getPsychologyCard ( PsychologyCard psychologyCard,
                                              String token,
                                              ApiResponseModel apiResponseModel ) {
        try {
            psychologyCard.setForeignerList(
                    this.stringToArrayList( Unirest.get(
                                    this.getConfig().getAPI_FOR_TRAIN_TICKET_CONSUMER_SERVICE() +
                                            psychologyCard
                                                    .getPapilonData()
                                                    .get( 0 )
                                                    .getPassport() )
                            .header( "Authorization", "Bearer " + token )
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
                    "Data was not found" );
            return psychologyCard; }
        return psychologyCard; }

    public PsychologyCard getPsychologyCard ( Results results,
                                              ApiResponseModel apiResponseModel ) { // returns the card in case of Person
        PsychologyCard psychologyCard = new PsychologyCard();
        try {
            this.setFamilyData( results, psychologyCard );
            psychologyCard.setPapilonData( results.getResults() );
            psychologyCard.setViolationList( results.getViolationList() );
            this.getGetPinpp().apply( results
                            .getResults()
                            .get( 0 )
                            .getPersonal_code() )
                    .subscribe( psychologyCard::setPinpp );

            this.getDeserialize().apply( psychologyCard
                            .getPinpp()
                            .getCadastre() )
                    .subscribe( psychologyCard::setModelForCadastr );

            this.getGetImageByPinfl().apply( results
                            .getResults()
                            .get( 0 )
                            .getPersonal_code() )
                    .subscribe( psychologyCard::setPersonImage );

            this.getGetModelForCarList().apply( results
                            .getResults()
                            .get( 0 )
                            .getPersonal_code() )
                    .subscribe( psychologyCard::setModelForCarList );

            this.getSetPersonPrivateData().accept( psychologyCard );
            this.getFindAllDataAboutCar().accept( psychologyCard );
            this.getSaveUserUsageLog().accept( new UserRequest( psychologyCard, apiResponseModel ) );
            return psychologyCard;
        } catch ( Exception e ) { return psychologyCard; } }

    public PsychologyCard getPsychologyCard ( com.ssd.mvd.entity.modelForPassport.Data data,
                                              ApiResponseModel apiResponseModel ) {
        PsychologyCard psychologyCard = new PsychologyCard();
        if ( data.getPerson() == null ) return psychologyCard;
        psychologyCard.setModelForPassport( data );
        FindFaceComponent
                .getInstance()
                .getViolationListByPinfl( data.getPerson().getPinpp() )
                .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                        error.getMessage(), object ) ) )
                .onErrorReturn( new ArrayList() )
                .subscribe( value -> psychologyCard.setViolationList( value != null ? value : new ArrayList<>() ) );

        FindFaceComponent
                .getInstance()
                .getFamilyMembersData( data.getPerson().getPinpp() )
                .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                        error.getMessage(), object ) ) )
                .onErrorReturn( new Results() )
                .subscribe( results -> this.setFamilyData( results, psychologyCard ) );

        this.getGetPinpp().apply( data.getPerson().getPinpp() )
                .subscribe( psychologyCard::setPinpp );
        this.getGetImageByPinfl().apply( data.getPerson().getPinpp() )
                .subscribe( psychologyCard::setPersonImage );
        this.getGetModelForCarList().apply( data.getPerson().getPinpp() )
                .subscribe( psychologyCard::setModelForCarList );
        this.getGetModelForAddress().apply( data.getPerson().getPCitizen() )
                .subscribe( psychologyCard::setModelForAddress );
        this.getDeserialize().apply( psychologyCard.getPinpp().getCadastre() )
                .subscribe( psychologyCard::setModelForCadastr );
        this.getFindAllDataAboutCar().accept( psychologyCard );
        this.getSaveUserUsageLog().accept( new UserRequest( psychologyCard, apiResponseModel ) );
        return psychologyCard; }

    @Override
    public void run () {
        while ( serDes != null ) {
            this.updateTokens();
            try { TimeUnit.HOURS.sleep( 3 ); } catch ( InterruptedException e ) { e.printStackTrace(); } } }
}