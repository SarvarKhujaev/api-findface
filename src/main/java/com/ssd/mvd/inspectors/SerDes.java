package com.ssd.mvd.inspectors;

import java.util.*;
import java.util.function.*;
import java.util.concurrent.TimeUnit;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicReference;

import reactor.netty.ByteBufMono;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientResponse;

import reactor.netty.ByteBufFlux;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.constants.Methods;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.interfaces.RequestCommonMethods;

import com.ssd.mvd.entity.*;
import com.ssd.mvd.request.*;
import com.ssd.mvd.entity.modelForGai.*;
import com.ssd.mvd.entity.foreigner.Foreigner;
import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.entity.modelForCadastr.Data;
import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
import com.ssd.mvd.entity.boardCrossing.CrossBoard;
import com.ssd.mvd.entity.response.ApiResponseModel;
import com.ssd.mvd.entity.boardCrossing.CrossBoardInfo;
import com.ssd.mvd.publisher.CustomPublisherForRequest;
import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.modelForFioOfPerson.PersonTotalDataByFIO;

@lombok.Data
@com.ssd.mvd.annotations.ImmutableEntityAnnotation
@lombok.EqualsAndHashCode( callSuper = true, cacheStrategy = lombok.EqualsAndHashCode.CacheStrategy.LAZY )
public final class SerDes extends RetryInspector {
    private static AtomicReference< Thread > thread;
    private static SerDes serDes = new SerDes();

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized SerDes getSerDes () {
        return serDes != null ? serDes : ( serDes = new SerDes() );
    }

    private SerDes () {
        super( SerDes.class );

        Unirest.setObjectMapper( new ObjectMapper() {
            private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

            @Override
            public String writeValue( @lombok.NonNull final Object o ) {
                try {
                    return this.objectMapper.writeValueAsString( o );
                } catch ( final JsonProcessingException e ) {
                    throw new RuntimeException( e );
                }
            }

            @Override
            public <T> T readValue( @lombok.NonNull final String s, @lombok.NonNull final Class<T> aClass ) {
                try {
                    return this.objectMapper.readValue( s, aClass );
                } catch ( final JsonProcessingException e ) {
                    throw new RuntimeException(e);
                }
            }
        } );

        Config.getHeaders().get().put( "accept", "application/json" );

        thread = EntitiesInstances.generateAtomicEntity(
                new Thread(
                        () -> {
                            while ( thread.get().isAlive() ) {
                                this.updateTokens();
                                try {
                                    TimeUnit.MINUTES.sleep( Config.waitingMins );
                                    EntitiesInstances.SEMAPHORE.get().release();
                                }
                                catch ( final InterruptedException e ) {
                                    this.close( e );
                                }
                            }
                        }
                )
        );
        thread.get().setName( this.getClass().getName() );
        thread.get().start();
        this.updateTokens();
    }

    @lombok.NonNull
    @lombok.Synchronized
    public synchronized SerDes updateTokens () {
        super.logging( Methods.UPDATE_TOKENS );

        Config.getFields().get().put( "Login", Config.getLOGIN_FOR_GAI_TOKEN() );
        Config.getFields().get().put( "Password" , Config.getPASSWORD_FOR_GAI_TOKEN() );
        Config.getFields().get().put( "CurrentSystem", Config.getCURRENT_SYSTEM_FOR_GAI() );

        try {
            this.clean();
            super.setTokenForGai(
                    String.valueOf(
                            Unirest.post( Config.getAPI_FOR_GAI_TOKEN() )
                                    .fields( Config.getFields().get() )
                                    .asJson()
                                    .getBody()
                                    .getObject()
                                    .get( "access_token" )
                    )
            );
            super.setTokenForPassport( Config.tokenForGai );
            super.setWaitingMins( 180 );
            super.setFlag( true );
            return this;
        }
        catch ( final Exception e ) {
            super.setFlag( false );
            super.setWaitingMins( 3 );
            super.saveErrorLog( e.getMessage() );
            super.saveErrorLog( Methods.UPDATE_TOKENS, "access_token", "Error: " + e.getMessage() );
        } finally {
            super.close();
        }

        return this;
    }

    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_, _, _, _, _ -> !null" )
    private synchronized <T> Mono<T> generate (
            @lombok.NonNull final String searchedValue,
            @lombok.NonNull final ByteBufMono content,
            @lombok.NonNull final HttpClientResponse res,
            @lombok.NonNull final Function< String, Mono< T > > customFunction,
            @lombok.NonNull final EntityCommonMethods< T > entityCommonMethods
    ) {
        return switch ( res.status().code() ) {
            case 401 -> customFunction.apply( searchedValue );
            case 501 | 502 | 503 -> super.saveErrorLog( res.status().toString(), entityCommonMethods );
            default -> super.checkResponse( res, content )
                    ? content
                    .asString()
                    .map( entityCommonMethods::generate )
                    : super.convert( entityCommonMethods.generate( searchedValue, Errors.DATA_NOT_FOUND ) );
        };
    }

    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_, _ -> !null" )
    private synchronized < T, U > WeakReference< ByteBufFlux > generate (
            @lombok.NonNull final U object,
            @lombok.NonNull final RequestCommonMethods< T, U > request
    ) {
        return EntitiesInstances.generateWeakEntity(
                ByteBufFlux.fromString(
                        new CustomPublisherForRequest( object, request )
                )
        );
    }

    private final Function< String, Mono< CrossBoardInfo > > getCrossBoardInfo =
            SerialNumber -> Config.HTTP_CLIENT
                    .post()
                    .uri( EntitiesInstances.CROSS_BOARD_INFO.get().getMethodName().getMethodApi() )
                    .send( this.generate( SerialNumber, EntitiesInstances.REQUEST_FOR_BOARD_CROSSING.get() ).get() )
                    .responseSingle(
                            ( res, content ) -> this.generate(
                                    SerialNumber,
                                    content,
                                    res,
                                    this.updateTokens().getGetCrossBoardInfo(),
                                    EntitiesInstances.CROSS_BOARD_INFO.get()
                            )
                    ).retryWhen( super.retry( EntitiesInstances.CROSS_BOARD_INFO.get() ) )
                    .onErrorResume(
                            throwable -> super.completeError( EntitiesInstances.CONNECT_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.CROSS_BOARD_INFO.get() )
                    ).onErrorResume(
                            throwable -> super.completeError( EntitiesInstances.ILLEGAL_ARGUMENT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.CROSS_BOARD_INFO.get() )
                    ).doOnError( e -> super.logging( e, EntitiesInstances.CROSS_BOARD_INFO.get(), SerialNumber ) )
                    .onErrorReturn( super.completeError( EntitiesInstances.CROSS_BOARD_INFO.get() ) );

    private final Function< String, String > base64ToLink = base64 -> {
            final WeakReference< HttpResponse< JsonNode > > response;
            Config.getFields().get().put( "photo", base64 );
            Config.getFields().get().put( "serviceName", "psychologyCard" );

            try {
                super.logging( "Converting image to Link in: " + Methods.CONVERT_BASE64_TO_LINK );

                response = EntitiesInstances.generateWeakEntity(
                        Unirest.post( Config.getBASE64_IMAGE_TO_LINK_CONVERTER_API() )
                                .header("Content-Type", "application/json")
                                .body( "{\r\n    \"serviceName\" : \"psychologyCard\",\r\n    \"photo\" : \"" + base64 + "\"\r\n}" )
                                .asJson()
                );

                return response.get().getStatus() == 200
                        ? response.get()
                        .getBody()
                        .getObject()
                        .get( "data" )
                        .toString()
                        : Errors.DATA_NOT_FOUND.name();
            }
            catch ( final UnirestException e ) {
                super.saveErrorLog(
                        Methods.CONVERT_BASE64_TO_LINK,
                        Methods.CONVERT_BASE64_TO_LINK.name(),
                        "Error: " + e.getMessage()
                );

                return Errors.SERVICE_WORK_ERROR.name();
            } finally {
                Config.getFields().get().clear();
                super.close();
            }
    };

    private final Function< String, Mono< Pinpp > > getPinpp = pinfl -> Config.HTTP_CLIENT
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForPassport ) )
            .get()
            .uri( EntitiesInstances.PINPP.get().getMethodName().getMethodApi() + pinfl )
            .responseSingle(
                    ( res, content ) -> this.generate(
                            pinfl,
                            content,
                            res,
                            this.updateTokens().getGetPinpp(),
                            EntitiesInstances.PINPP.get()
                    )
            ).retryWhen( super.retry( EntitiesInstances.PINPP.get() ) )
            .onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.CONNECT_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.PINPP.get() )
            ).onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.ILLEGAL_ARGUMENT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.PINPP.get() )
            ).doOnError( throwable -> super.logging( throwable, EntitiesInstances.PINPP.get(), pinfl ) )
            .doOnSuccess( value -> super.logging( Methods.GET_PINPP, value ) )
            .doOnSubscribe( value -> super.logging( Config.getAPI_FOR_PINPP() ) );

    private final Function< String, Mono< Data > > getCadaster = cadaster -> Config.HTTP_CLIENT
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForPassport ) )
            .post()
            .send( this.generate( cadaster, new RequestForCadaster() ).get() )
            .uri( EntitiesInstances.CADASTR.get().getMethodName().getMethodApi() )
            .responseSingle(
                    ( res, content ) -> this.generate(
                            cadaster,
                            content,
                            res,
                            this.updateTokens().getGetCadaster(),
                            EntitiesInstances.CADASTR.get()
                    )
            ).retryWhen( super.retry( EntitiesInstances.CADASTR.get() ) )
            .onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.CONNECT_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.CADASTR.get() )
            ).onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.ILLEGAL_ARGUMENT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.CADASTR.get() )
            ).doOnError( e -> super.logging( e, EntitiesInstances.CADASTR.get(), cadaster ) )
            .doOnSuccess( value -> super.logging( Methods.CADASTER, value ) )
            .doOnSubscribe( value -> super.logging( EntitiesInstances.CADASTR.get().getMethodName().getMethodApi() ) )
            .onErrorReturn( super.completeError( EntitiesInstances.CADASTR.get().generate() ) );

    private final Function< String, Mono< String > > getImageByPinfl = pinfl -> Config.HTTP_CLIENT
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForGai ) )
            .get()
            .uri( Config.getAPI_FOR_PERSON_IMAGE() + pinfl )
            .responseSingle( ( res, content ) -> switch ( res.status().code() ) {
                case 401 -> this.updateTokens().getGetImageByPinfl().apply( pinfl );
                case 501 | 502 | 503 -> super.convert( res.status().toString() );
                default -> super.checkResponse( res, content )
                        ? content
                        .asString()
                        .map( s -> s.substring( s.indexOf( "Data" ) + 7, s.indexOf( ",\"AnswereId" ) - 1 ) )
                        : super.convert( Errors.DATA_NOT_FOUND.name() );
            } )
            .retryWhen( super.retry( EntitiesInstances.PINPP.get() ) )
            .onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.CONNECT_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(), Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED )
            ).onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.ILLEGAL_ARGUMENT_EXCEPTION_ATOMIC_REFERENCE.get(), Errors.TOO_MANY_RETRIES_ERROR )
            ).doOnError( super::logging )
            .doOnSubscribe( value -> super.logging( Config.getAPI_FOR_PERSON_IMAGE() ) )
            .onErrorReturn( Errors.DATA_NOT_FOUND.name() );

    private final Function< String, Mono< ModelForAddress > > getModelForAddress = pinfl -> Config.HTTP_CLIENT
            .headers( h -> h.add( "Authorization", "Bearer " + EntitiesInstances.MODEL_FOR_ADDRESS.get().getMethodName().getMethodApi() ) )
            .post()
            .uri( EntitiesInstances.MODEL_FOR_ADDRESS.get().getMethodName().getMethodApi() )
            .send( this.generate( pinfl, new RequestForModelOfAddress() ).get() )
            .responseSingle(
                    ( res, content ) -> this.generate(
                            pinfl,
                            content,
                            res,
                            this.updateTokens().getGetModelForAddress(),
                            EntitiesInstances.MODEL_FOR_ADDRESS.get()
                    )
            ).retryWhen( super.retry( EntitiesInstances.MODEL_FOR_ADDRESS.get() ) )
            .onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.CONNECT_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.MODEL_FOR_ADDRESS.get() )
            ).onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.CONNECT_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.MODEL_FOR_ADDRESS.get() )
            ).doOnError( e -> super.logging( e, EntitiesInstances.MODEL_FOR_ADDRESS.get(), pinfl ) )
            .doOnSuccess( value -> super.logging( Methods.GET_MODEL_FOR_ADDRESS, value ) )
            .doOnSubscribe( value -> super.logging( EntitiesInstances.MODEL_FOR_ADDRESS.get().getMethodName().getMethodApi() ) )
            .onErrorReturn( super.completeError( EntitiesInstances.MODEL_FOR_ADDRESS.get() ) );

    private final Function< String, Mono< ModelForPassport > > getModelForPassport =
            passportData -> Config.HTTP_CLIENT
                    .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForPassport ) )
                    .post()
                    .uri( EntitiesInstances.MODEL_FOR_PASSPORT.get().getMethodName().getMethodApi() )
                    .send( this.generate( passportData, new RequestForPassport() ).get() )
                    .responseSingle(
                            ( res, content ) -> this.generate(
                                    passportData,
                                    content,
                                    res,
                                    this.updateTokens().getGetModelForPassport(),
                                    EntitiesInstances.MODEL_FOR_PASSPORT.get()
                            )
                    ).retryWhen( super.retry( EntitiesInstances.MODEL_FOR_PASSPORT.get() ) )
                    .onErrorResume(
                            throwable -> super.completeError( EntitiesInstances.CONNECT_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.MODEL_FOR_PASSPORT.get() )
                    ).onErrorResume(
                            throwable -> super.completeError( EntitiesInstances.ILLEGAL_ARGUMENT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.MODEL_FOR_PASSPORT.get() )
                    ).doOnError( e -> super.logging( e, EntitiesInstances.MODEL_FOR_PASSPORT.get(), passportData ) )
                    .doOnSuccess( value -> super.logging( Methods.GET_MODEL_FOR_PASSPORT, value ) )
                    .doOnSubscribe( value -> super.logging( EntitiesInstances.MODEL_FOR_PASSPORT.get().getMethodName().getMethodApi() ) )
                    .onErrorReturn( super.completeError( EntitiesInstances.MODEL_FOR_PASSPORT.get() ) );

    private final Function< String, Mono< Insurance > > insurance = gosno -> Config.HTTP_CLIENT
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForGai ) )
            .get()
            .uri( EntitiesInstances.INSURANCE.get().getMethodName().getMethodApi() + gosno )
            .responseSingle(
                    ( res, content ) -> this.generate(
                            gosno,
                            content,
                            res,
                            this.updateTokens().getInsurance(),
                            EntitiesInstances.INSURANCE.get()
                    )
            ).retryWhen( super.retry( EntitiesInstances.INSURANCE.get() ) )
            .onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.CONNECT_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.INSURANCE.get() )
            ).onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.ILLEGAL_ARGUMENT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.INSURANCE.get() )
            ).doOnError( e -> super.logging( e, EntitiesInstances.INSURANCE.get(), gosno ) )
            .doOnSuccess( value -> super.logging( EntitiesInstances.INSURANCE.get().getMethodName(), value ) )
            .doOnSubscribe( value -> super.logging( EntitiesInstances.INSURANCE.get().getMethodName().getMethodApi() ) )
            .onErrorReturn( super.completeError( EntitiesInstances.INSURANCE.get() ) );

    private final Function< String, Mono< ModelForCar > > getVehicleData = gosno -> Config.HTTP_CLIENT
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForGai ) )
            .get()
            .uri( EntitiesInstances.MODEL_FOR_CAR.get().getMethodName().getMethodApi() + gosno )
            .responseSingle(
                    ( res, content ) -> this.generate(
                            gosno,
                            content,
                            res,
                            this.updateTokens().getGetVehicleData(),
                            EntitiesInstances.MODEL_FOR_CAR.get()
                    )
            ).retryWhen( super.retry( EntitiesInstances.MODEL_FOR_CAR.get() ) )
            .onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.CONNECT_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.MODEL_FOR_CAR.get() )
            ).onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.ILLEGAL_ARGUMENT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.MODEL_FOR_CAR.get() )
            ).doOnError( e -> super.logging( e, EntitiesInstances.MODEL_FOR_CAR.get(), gosno ) )
            .doOnSuccess( value -> super.logging( EntitiesInstances.MODEL_FOR_CAR.get().getMethodName(), value ) )
            .doOnSubscribe( value -> super.logging( EntitiesInstances.MODEL_FOR_CAR.get().getMethodName().getMethodApi() ) )
            .onErrorReturn( super.completeError( EntitiesInstances.MODEL_FOR_CAR.get() ) );

    private final Function< String, Mono< Tonirovka > > getVehicleTonirovka = gosno -> Config.HTTP_CLIENT
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForGai ) )
            .get()
            .uri( EntitiesInstances.TONIROVKA.get().getMethodName().getMethodApi() + gosno )
            .responseSingle(
                    ( res, content ) -> this.generate(
                            gosno,
                            content,
                            res,
                            this.updateTokens().getGetVehicleTonirovka(),
                            EntitiesInstances.TONIROVKA.get()
                    )
            ).retryWhen( super.retry( EntitiesInstances.TONIROVKA.get() ) )
            .onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.CONNECT_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.TONIROVKA.get() )
            ).onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.ILLEGAL_ARGUMENT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.TONIROVKA.get() )
            ).doOnError( e -> super.logging( e, EntitiesInstances.TONIROVKA.get(), gosno ) )
            .doOnSuccess( value -> super.logging( EntitiesInstances.TONIROVKA.get().getMethodName(), value ) )
            .doOnSubscribe( value -> super.logging( EntitiesInstances.MODEL_FOR_CAR.get().getMethodName().getMethodApi() ) )
            .onErrorReturn( super.completeError( EntitiesInstances.TONIROVKA.get().generate() ) );

    private final Function< String, Mono< ViolationsList > > getViolationList = gosno -> Config.HTTP_CLIENT
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForGai ) )
            .get()
            .uri( EntitiesInstances.VIOLATIONS_LIST.get().getMethodName().getMethodApi() + gosno )
            .responseSingle(
                    ( res, content ) -> this.generate(
                            gosno,
                            content,
                            res,
                            this.updateTokens().getGetViolationList(),
                            EntitiesInstances.VIOLATIONS_LIST.get()
                    )
            ).retryWhen( super.retry( EntitiesInstances.VIOLATIONS_LIST.get() ) )
            .onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.CONNECT_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.VIOLATIONS_LIST.get() )
            ).onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.ILLEGAL_ARGUMENT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.VIOLATIONS_LIST.get() )
            ).doOnError( e -> super.logging( e, EntitiesInstances.VIOLATIONS_LIST.get(), gosno ) )
            .doOnSuccess( value -> super.logging( EntitiesInstances.VIOLATIONS_LIST.get().getMethodName(), value ) )
            .doOnSubscribe( value -> super.logging( EntitiesInstances.VIOLATIONS_LIST.get().getMethodName().getMethodApi() ) )
            .onErrorReturn( super.completeError( EntitiesInstances.VIOLATIONS_LIST.get().generate() ) );

    private final Function< String, Mono< DoverennostList > > getDoverennostList = gosno -> Config.HTTP_CLIENT
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForGai ) )
            .get()
            .uri( EntitiesInstances.DOVERENNOST_LIST.get().getMethodName().getMethodApi() + gosno )
            .responseSingle(
                    ( res, content ) -> this.generate(
                            gosno,
                            content,
                            res,
                            this.updateTokens().getGetDoverennostList(),
                            EntitiesInstances.DOVERENNOST_LIST.get()
                    )
            ).retryWhen( super.retry( EntitiesInstances.DOVERENNOST_LIST.get() ) )
            .onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.CONNECT_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.DOVERENNOST_LIST.get() )
            ).onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.ILLEGAL_ARGUMENT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.DOVERENNOST_LIST.get() )
            ).doOnError( e -> super.logging( e, EntitiesInstances.DOVERENNOST_LIST.get(), gosno ) )
            .doOnSuccess( value -> super.logging( EntitiesInstances.DOVERENNOST_LIST.get().getMethodName(), value ) )
            .doOnSubscribe( value -> super.logging( EntitiesInstances.DOVERENNOST_LIST.get().getMethodName().getMethodApi() ) )
            .onErrorReturn( super.completeError( EntitiesInstances.DOVERENNOST_LIST.get() ) );

    private final Function< String, Mono< ModelForCarList > > getModelForCarList = pinfl -> Config.HTTP_CLIENT
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForGai ) )
            .get()
            .uri( EntitiesInstances.MODEL_FOR_CAR_LIST.get().getMethodName().getMethodApi() + pinfl )
            .responseSingle(
                    ( res, content ) -> this.generate(
                            pinfl,
                            content,
                            res,
                            this.updateTokens().getGetModelForCarList(),
                            EntitiesInstances.MODEL_FOR_CAR_LIST.get()
                    )
            ).retryWhen( super.retry( EntitiesInstances.MODEL_FOR_CAR_LIST.get() ) )
            .onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.CONNECT_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.MODEL_FOR_CAR_LIST.get() )
            ).onErrorResume(
                    throwable -> super.completeError( EntitiesInstances.ILLEGAL_ARGUMENT_EXCEPTION_ATOMIC_REFERENCE.get(), EntitiesInstances.MODEL_FOR_CAR_LIST.get() )
            ).doOnError( e -> super.logging( e, EntitiesInstances.MODEL_FOR_CAR_LIST.get(), pinfl ) )
            .doOnSuccess( value -> super.logging( EntitiesInstances.MODEL_FOR_CAR_LIST.get().getMethodName(), value ) )
            .doOnSubscribe( value -> super.logging( EntitiesInstances.MODEL_FOR_CAR_LIST.get().getMethodName().getMethodApi() ) )
            .onErrorReturn( super.completeError( EntitiesInstances.MODEL_FOR_CAR_LIST.get().generate() ) );

    private final Function< ModelForCarList, Mono< ModelForCarList > > findAllAboutCarList = modelForCarList ->
            super.convertValuesToParallelFluxWithMap(
                    modelForCarList.getModelForCarList(),
                    modelForCar -> Mono.zip(
                            this.getInsurance().apply( modelForCar.getPlateNumber() ),
                            this.getGetVehicleTonirovka().apply( modelForCar.getPlateNumber() ),
                            this.getGetDoverennostList().apply( modelForCar.getPlateNumber() )
                    ).map( tuple3 -> modelForCar.save( tuple3, modelForCarList ) )
            ).take( 1 )
            .mapNotNull( Mono::block )
            .single();

    private final Function< PsychologyCard, Mono< PsychologyCard > > findAllDataAboutCar = psychologyCard ->
            super.check( psychologyCard )
                    ? super.convertValuesToParallelFluxWithMap(
                            psychologyCard
                                    .getModelForCarList()
                                    .getModelForCarList(),
                            modelForCar -> Mono.zip(
                                    this.getInsurance().apply( modelForCar.getPlateNumber() ),
                                    this.getGetVehicleTonirovka().apply( modelForCar.getPlateNumber() ),
                                    this.getGetDoverennostList().apply( modelForCar.getPlateNumber() )
                            ).map( tuple3 -> modelForCar.save( tuple3, psychologyCard ) )
                    ).take( 1 )
                    .mapNotNull( Mono::block )
                    .single()
                    : super.convert( psychologyCard );

    private final Function< PsychologyCard, Mono< PsychologyCard > > setPersonPrivateDataAsync = psychologyCard ->
            super.checkPinpp( psychologyCard )
                    ? this.getGetCadaster()
                    .apply( psychologyCard.getPinpp().getCadastre() )
                    .flatMap( data -> super.checkCadastor( psychologyCard.save( data ) )
                            ? super.convertValuesToParallelFluxWithFilter(
                                    psychologyCard
                                            .getModelForCadastr()
                                            .getPermanentRegistration(),
                                    person -> super.checkPerson( person, psychologyCard.getPinpp() )
                            ).take( 1 )
                            .single()
                            .flatMap(
                                    person -> Mono.zip(
                                            this.getGetModelForAddress().apply( person.getPCitizen() ),
                                            this.getGetModelForPassport().apply(
                                                    String.join(
                                                            EMPTY,
                                                            person.getPPsp(),
                                                            person.getPDateBirth()
                                                    )
                                            )
                                    )
                            ).map( psychologyCard::save )
                            .onErrorResume(
                                    throwable -> super.convert(
                                            EntitiesInstances.PSYCHOLOGY_CARD.get().generate(
                                                    throwable.getMessage(),
                                                    Errors.SERVICE_WORK_ERROR
                                            )
                                    )
                            )
                            : super.convert( psychologyCard )
                    )
                    : super.convert( psychologyCard );

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _, _ -> !null" )
    public Mono< PsychologyCard > getPsychologyCard (
            @lombok.NonNull final String token,
            @lombok.NonNull final PsychologyCard psychologyCard,
            @lombok.NonNull final ApiResponseModel apiResponseModel
    ) {
        try {
            Config.getHeaders().get().put( "Authorization", "Bearer " + token );

            psychologyCard.setForeignerList(
                    Config.stringToArrayList(
                            Unirest.get(
                                    String.join(
                                            EMPTY,
                                            Config.getAPI_FOR_TRAIN_TICKET_CONSUMER_SERVICE(),
                                            psychologyCard.getPapilonData().get( 0 ).getPassport()
                                    )
                            ).headers( Config.getHeaders().get() )
                            .asJson()
                            .getBody()
                            .getObject()
                            .get( "data" )
                            .toString(),
                            Foreigner[].class
                    )
            );

            super.saveUserUsageLog( psychologyCard, apiResponseModel );
        } catch ( final Exception e ) {
            super.saveErrorLog(
                    Methods.GET_PSYCHOLOGY_CARD,
                    psychologyCard.getPapilonData().get( 0 ).getPassport(),
                    Errors.DATA_NOT_FOUND.name()
            );
        } finally {
            Config.getHeaders().get().clear();
            super.close();
        }

        return super.convert( psychologyCard );
    }

    private final Function< FIO, Mono< PersonTotalDataByFIO > > getPersonTotalDataByFIO = fio -> Config.HTTP_CLIENT
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForFio ) )
            .post()
            .uri( Config.getAPI_FOR_PERSON_DATA_FROM_ZAKS() )
            .send( this.generate( fio, new RequestForFio() ).get() )
            .responseSingle( ( res, content ) -> res.status().code() == 401
                    ? this.updateTokens().getGetPersonTotalDataByFIO().apply( fio )
                    : super.checkResponse( res, content )
                            ? content
                            .asString()
                            .mapNotNull( s -> {
                                final WeakReference< PersonTotalDataByFIO > person = EntitiesInstances.generateWeakEntity( deserialize( s, PersonTotalDataByFIO.class ) );

                                if ( objectIsNotNull( person.get() ) && isCollectionNotEmpty( person.get().getData() ) ) {
                                    super.analyze(
                                            person.get().getData(),
                                            person1 -> this.getGetImageByPinfl()
                                                    .apply( person1.getPinpp() )
                                                    .subscribe( person1::setPersonImage )
                                    );
                                }

                                return person.get();
                            } )
                            : super.convert( super.completeError( EntitiesInstances.PERSON_TOTAL_DATA_BY_FIO.get() ) )
            )
            .doOnError( e -> super.logging( e, EntitiesInstances.PERSON_TOTAL_DATA_BY_FIO.get(), fio.getName() ) )
            .onErrorReturn( super.completeError( EntitiesInstances.PERSON_TOTAL_DATA_BY_FIO.get() ) );

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
                            ).map( tuple1 -> super.saveUserUsageLog( psychologyCard, apiResponseModel ) )
                    )
                    : super.convert( super.completeError( EntitiesInstances.PSYCHOLOGY_CARD.get() ) );

    private final Function< ApiResponseModel, Mono< PsychologyCard > > getPsychologyCardByPinflInitial =
            apiResponseModel -> this.checkParam( apiResponseModel.getStatus().getMessage() )
                    ? Mono.zip(
                            this.getGetPinpp().apply( apiResponseModel.getStatus().getMessage() ),
                            this.getGetImageByPinfl().apply( apiResponseModel.getStatus().getMessage() )
                    ).map( tuple -> super.saveUserUsageLog( PsychologyCard.generate( tuple ), apiResponseModel ) )
                    : super.convert( super.completeError( EntitiesInstances.PSYCHOLOGY_CARD.get() ) );

    private final BiFunction< Results, ApiResponseModel, Mono< PsychologyCard > > getPsychologyCardByImage =
            ( results, apiResponseModel ) -> Mono.zip(
                            this.getGetPinpp().apply( results.getResults().get( 0 ).getPersonal_code() ),
                            this.getGetImageByPinfl().apply( results.getResults().get( 0 ).getPersonal_code() ),
                            this.getGetModelForCarList().apply( results.getResults().get( 0 ).getPersonal_code() )
                    ).map( tuple -> PsychologyCard.generate( results, tuple ) )
                    .flatMap( psychologyCard -> Mono.zip(
                                    this.getFindAllDataAboutCar().apply( psychologyCard ),
                                    this.getSetPersonPrivateDataAsync().apply( psychologyCard )
                            ).map( tuple1 -> super.saveUserUsageLog( psychologyCard, apiResponseModel ) )
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
                                    .map( psychologyCard -> super.saveUserUsageLog( psychologyCard, apiResponseModel ) )
                    )
                    : super.convert( super.completeError( EntitiesInstances.PSYCHOLOGY_CARD.get() ) );

    private final Function< CrossBoardInfo, Mono< CrossBoardInfo > > analyzeCrossData = crossBoardInfo ->
            super.convertValuesToParallelFluxWithMap(
                    crossBoardInfo
                            .getData()
                            .get( 0 )
                            .getCrossBoardList(),
                    crossBoard -> crossBoard.save( crossBoardInfo.getData().get( 0 ).getPerson().getNationalityid() )
            ).collectList()
            .mapNotNull( crossBoards -> {
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
        thread.get().interrupt();
        super.clearAllEntities();

        super.close();
        this.clean();
    }

    @Override
    public void close( @lombok.NonNull final Throwable throwable ) {
        super.logging( throwable );
        this.close();
    }
}