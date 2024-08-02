package com.ssd.mvd.inspectors;

import java.util.*;
import java.util.function.*;
import java.util.concurrent.TimeUnit;

import reactor.netty.ByteBufMono;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientResponse;

import io.netty.channel.ConnectTimeoutException;

import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClient;

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
import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.entity.modelForCadastr.Data;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
import com.ssd.mvd.entity.boardCrossing.CrossBoard;
import com.ssd.mvd.interfaces.RequestCommonMethods;
import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.entity.boardCrossing.CrossBoardInfo;
import com.ssd.mvd.publisher.CustomPublisherForRequest;
import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.modelForFioOfPerson.PersonTotalDataByFIO;

@lombok.Data
public final class SerDes extends RetryInspector implements ServiceCommonMethods {
    private Thread thread;

    private static SerDes serDes = new SerDes();
    private final HttpClient httpClient = Config.HTTP_CLIENT;

    public static SerDes getSerDes () {
        return serDes != null ? serDes : ( serDes = new SerDes() );
    }

    private SerDes () {
        Unirest.setObjectMapper( new ObjectMapper() {
            private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

            @Override
            public String writeValue( final Object o ) {
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

        Config.headers.put( "accept", "application/json" );

        this.setThread(
                new Thread(
                        () -> {
                            while ( this.getThread().isAlive() ) {
                                this.getUpdateTokens().get();
                                try {
                                    TimeUnit.MINUTES.sleep( Config.waitingMins );
                                    EntitiesInstances.SEMAPHORE.release();
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

            Config.fields.put( "Login", super.getLOGIN_FOR_GAI_TOKEN() );
            Config.fields.put( "Password" , super.getPASSWORD_FOR_GAI_TOKEN() );
            Config.fields.put( "CurrentSystem", super.getCURRENT_SYSTEM_FOR_GAI() );

            try {
                super.setTokenForGai(
                        String.valueOf(
                                Unirest.post( super.getAPI_FOR_GAI_TOKEN() )
                                    .fields( Config.fields )
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
    };

    private < T extends StringOperations > Mono<T> generate (
            final String searchedValue,
            final ByteBufMono content,
            final HttpClientResponse res,
            final Function< String, Mono< T > > customFunction,
            final EntityCommonMethods< T > entityCommonMethods
    ) {
        return switch ( res.status().code() ) {
            case 401 -> customFunction.apply( searchedValue );
            case 501 | 502 | 503 -> ( Mono< T > ) super.saveErrorLog.apply( res.status().toString(), entityCommonMethods.getMethodName() );
            default -> super.checkResponse( res, content )
                    ? content
                    .asString()
                    .map( entityCommonMethods::generate )
                    : super.convert( entityCommonMethods.generate( searchedValue, Errors.DATA_NOT_FOUND ) );
        };
    }

    private < T, U > ByteBufFlux generate (
            final U object,
            final RequestCommonMethods< T, U > request
    ) {
        return ByteBufFlux.fromString(
                new CustomPublisherForRequest( object, request )
        );
    }

    private final Function< String, Mono< CrossBoardInfo > > getCrossBoardInfo =
            SerialNumber -> this.getHttpClient()
                    .post()
                    .uri( EntitiesInstances.CROSS_BOARD_INFO.getMethodApi() )
                    .send( this.generate( SerialNumber, EntitiesInstances.REQUEST_FOR_BOARD_CROSSING ) )
                    .responseSingle(
                            ( res, content ) -> this.generate(
                                    SerialNumber,
                                    content,
                                    res,
                                    this.getUpdateTokens().get().getGetCrossBoardInfo(),
                                    EntitiesInstances.CROSS_BOARD_INFO
                            )
                    ).retryWhen( super.retry( EntitiesInstances.CROSS_BOARD_INFO ) )
                    .onErrorResume(
                            throwable -> super.completeError( new ConnectTimeoutException(), EntitiesInstances.CROSS_BOARD_INFO )
                    ).onErrorResume(
                            throwable -> super.completeError( new IllegalArgumentException(), EntitiesInstances.CROSS_BOARD_INFO )
                    ).doOnError( e -> super.logging( e, EntitiesInstances.CROSS_BOARD_INFO, SerialNumber ) )
                    .onErrorReturn( super.completeError( EntitiesInstances.CROSS_BOARD_INFO ) );

    private final Function< String, String > base64ToLink = base64 -> {
            final HttpResponse< JsonNode > response;
            Config.fields.put( "photo", base64 );
            Config.fields.put( "serviceName", "psychologyCard" );

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
                super.saveErrorLog(
                        Methods.CONVERT_BASE64_TO_LINK,
                        Methods.CONVERT_BASE64_TO_LINK.name(),
                        "Error: " + e.getMessage()
                );

                return Errors.SERVICE_WORK_ERROR.name();
            } finally {
                super.close();
            }
    };

    private final Function< String, Mono< Pinpp > > getPinpp = pinfl -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForPassport ) )
            .get()
            .uri( EntitiesInstances.PINPP.getMethodApi() + pinfl )
            .responseSingle(
                    ( res, content ) -> this.generate(
                            pinfl,
                            content,
                            res,
                            this.getUpdateTokens().get().getGetPinpp(),
                            EntitiesInstances.PINPP
                    )
            ).retryWhen( super.retry( EntitiesInstances.PINPP ) )
            .onErrorResume(
                    throwable -> super.completeError( new ConnectTimeoutException(), EntitiesInstances.PINPP )
            ).onErrorResume(
                    throwable -> super.completeError( new IllegalArgumentException(), EntitiesInstances.PINPP )
            ).doOnError( throwable -> super.logging( throwable, EntitiesInstances.PINPP, pinfl ) )
            .doOnSuccess( value -> super.logging( Methods.GET_PINPP, value ) )
            .doOnSubscribe( value -> super.logging( super.getAPI_FOR_PINPP() ) );

    private final Function< String, Mono< Data > > getCadaster = cadaster -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForPassport ) )
            .post()
            .send( this.generate( cadaster, new RequestForCadaster() ) )
            .uri( EntitiesInstances.CADASTR.getMethodApi() )
            .responseSingle(
                    ( res, content ) -> this.generate(
                            cadaster,
                            content,
                            res,
                            this.getUpdateTokens().get().getGetCadaster(),
                            EntitiesInstances.CADASTR
                    )
            ).retryWhen( super.retry( EntitiesInstances.CADASTR ) )
            .onErrorResume(
                    throwable -> super.completeError( new ConnectTimeoutException(), EntitiesInstances.CADASTR )
            ).onErrorResume(
                    throwable -> super.completeError( new IllegalArgumentException(), EntitiesInstances.CADASTR )
            ).doOnError( e -> super.logging( e, EntitiesInstances.CADASTR, cadaster ) )
            .doOnSuccess( value -> super.logging( Methods.CADASTER, value ) )
            .doOnSubscribe( value -> super.logging( EntitiesInstances.CADASTR.getMethodApi() ) )
            .onErrorReturn( super.completeError( EntitiesInstances.CADASTR.generate() ) );

    private final Function< String, Mono< String > > getImageByPinfl = pinfl -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForGai ) )
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
            .retryWhen( super.retry( EntitiesInstances.PINPP ) )
            .onErrorResume(
                    throwable -> super.completeError( new ConnectTimeoutException(), Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED )
            ).onErrorResume(
                    throwable -> super.completeError( new IllegalArgumentException(), Errors.TOO_MANY_RETRIES_ERROR )
            ).doOnError( super::logging )
            .doOnSubscribe( value -> super.logging( super.getAPI_FOR_PERSON_IMAGE() ) )
            .onErrorReturn( Errors.DATA_NOT_FOUND.name() );

    private final Function< String, Mono< ModelForAddress > > getModelForAddress = pinfl -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + EntitiesInstances.MODEL_FOR_ADDRESS.getMethodApi() ) )
            .post()
            .uri( EntitiesInstances.MODEL_FOR_ADDRESS.getMethodApi() )
            .send( this.generate( pinfl, new RequestForModelOfAddress() ) )
            .responseSingle(
                    ( res, content ) -> this.generate(
                            pinfl,
                            content,
                            res,
                            this.getUpdateTokens().get().getGetModelForAddress(),
                            EntitiesInstances.MODEL_FOR_ADDRESS
                    )
            ).retryWhen( super.retry( EntitiesInstances.MODEL_FOR_ADDRESS ) )
            .onErrorResume(
                    throwable -> super.completeError( new ConnectTimeoutException(), EntitiesInstances.MODEL_FOR_ADDRESS )
            ).onErrorResume(
                    throwable -> super.completeError( new ConnectTimeoutException(), EntitiesInstances.MODEL_FOR_ADDRESS )
            ).doOnError( e -> super.logging( e, EntitiesInstances.MODEL_FOR_ADDRESS, pinfl ) )
            .doOnSuccess( value -> super.logging( Methods.GET_MODEL_FOR_ADDRESS, value ) )
            .doOnSubscribe( value -> super.logging( EntitiesInstances.MODEL_FOR_ADDRESS.getMethodApi() ) )
            .onErrorReturn( super.completeError( EntitiesInstances.MODEL_FOR_ADDRESS ) );

    private final Function< String, Mono< ModelForPassport > > getModelForPassport =
            passportData -> this.getHttpClient()
                    .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForPassport ) )
                    .post()
                    .uri( EntitiesInstances.MODEL_FOR_PASSPORT.getMethodApi() )
                    .send( this.generate( passportData, new RequestForPassport() ) )
                    .responseSingle(
                            ( res, content ) -> this.generate(
                                    passportData,
                                    content,
                                    res,
                                    this.getUpdateTokens().get().getGetModelForPassport(),
                                    EntitiesInstances.MODEL_FOR_PASSPORT
                            )
                    ).retryWhen( super.retry( EntitiesInstances.MODEL_FOR_PASSPORT ) )
                    .onErrorResume(
                            throwable -> super.completeError( new ConnectTimeoutException(), EntitiesInstances.MODEL_FOR_PASSPORT )
                    ).onErrorResume(
                            throwable -> super.completeError( new IllegalArgumentException(), EntitiesInstances.MODEL_FOR_PASSPORT )
                    ).doOnError( e -> super.logging( e, EntitiesInstances.MODEL_FOR_PASSPORT, passportData ) )
                    .doOnSuccess( value -> super.logging( Methods.GET_MODEL_FOR_PASSPORT, value ) )
                    .doOnSubscribe( value -> super.logging( EntitiesInstances.MODEL_FOR_PASSPORT.getMethodApi() ) )
                    .onErrorReturn( super.completeError( EntitiesInstances.MODEL_FOR_PASSPORT ) );

    private final Function< String, Mono< Insurance > > insurance = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForGai ) )
            .get()
            .uri( EntitiesInstances.INSURANCE.getMethodApi() + gosno )
            .responseSingle(
                    ( res, content ) -> this.generate(
                            gosno,
                            content,
                            res,
                            this.getUpdateTokens().get().getInsurance(),
                            EntitiesInstances.INSURANCE
                    )
            ).retryWhen( super.retry( EntitiesInstances.INSURANCE ) )
            .onErrorResume(
                    throwable -> super.completeError( new ConnectTimeoutException(), EntitiesInstances.INSURANCE )
            ).onErrorResume(
                    throwable -> super.completeError( new IllegalArgumentException(), EntitiesInstances.INSURANCE )
            ).doOnError( e -> super.logging( e, EntitiesInstances.INSURANCE, gosno ) )
            .doOnSuccess( value -> super.logging( EntitiesInstances.INSURANCE.getMethodName(), value ) )
            .doOnSubscribe( value -> super.logging( EntitiesInstances.INSURANCE.getMethodApi() ) )
            .onErrorReturn( super.completeError( EntitiesInstances.INSURANCE ) );

    private final Function< String, Mono< ModelForCar > > getVehicleData = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForGai ) )
            .get()
            .uri( EntitiesInstances.MODEL_FOR_CAR.getMethodApi() + gosno )
            .responseSingle(
                    ( res, content ) -> this.generate(
                            gosno,
                            content,
                            res,
                            this.getUpdateTokens().get().getGetVehicleData(),
                            EntitiesInstances.MODEL_FOR_CAR
                    )
            ).retryWhen( super.retry( EntitiesInstances.MODEL_FOR_CAR ) )
            .onErrorResume(
                    throwable -> super.completeError( new ConnectTimeoutException(), EntitiesInstances.MODEL_FOR_CAR )
            ).onErrorResume(
                    throwable -> super.completeError( new IllegalArgumentException(), EntitiesInstances.MODEL_FOR_CAR )
            ).doOnError( e -> super.logging( e, EntitiesInstances.MODEL_FOR_CAR, gosno ) )
            .doOnSuccess( value -> super.logging( EntitiesInstances.MODEL_FOR_CAR.getMethodName(), value ) )
            .doOnSubscribe( value -> super.logging( EntitiesInstances.MODEL_FOR_CAR.getMethodApi() ) )
            .onErrorReturn( super.completeError( EntitiesInstances.MODEL_FOR_CAR ) );

    private final Function< String, Mono< Tonirovka > > getVehicleTonirovka = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForGai ) )
            .get()
            .uri( EntitiesInstances.TONIROVKA.getMethodApi() + gosno )
            .responseSingle(
                    ( res, content ) -> this.generate(
                            gosno,
                            content,
                            res,
                            this.getUpdateTokens().get().getGetVehicleTonirovka(),
                            EntitiesInstances.TONIROVKA
                    )
            ).retryWhen( super.retry( EntitiesInstances.TONIROVKA ) )
            .onErrorResume(
                    throwable -> super.completeError( new ConnectTimeoutException(), EntitiesInstances.TONIROVKA )
            ).onErrorResume(
                    throwable -> super.completeError( new IllegalArgumentException(), EntitiesInstances.TONIROVKA )
            ).doOnError( e -> super.logging( e, EntitiesInstances.TONIROVKA, gosno ) )
            .doOnSuccess( value -> super.logging( EntitiesInstances.TONIROVKA.getMethodName(), value ) )
            .doOnSubscribe( value -> super.logging( EntitiesInstances.MODEL_FOR_CAR.getMethodApi() ) )
            .onErrorReturn( super.completeError( EntitiesInstances.TONIROVKA.generate() ) );

    private final Function< String, Mono< ViolationsList > > getViolationList = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForGai ) )
            .get()
            .uri( EntitiesInstances.VIOLATIONS_LIST.getMethodApi() + gosno )
            .responseSingle(
                    ( res, content ) -> this.generate(
                            gosno,
                            content,
                            res,
                            this.getUpdateTokens().get().getGetViolationList(),
                            EntitiesInstances.VIOLATIONS_LIST
                    )
            ).retryWhen( super.retry( EntitiesInstances.VIOLATIONS_LIST ) )
            .onErrorResume(
                    throwable -> super.completeError( new ConnectTimeoutException(), EntitiesInstances.VIOLATIONS_LIST )
            ).onErrorResume(
                    throwable -> super.completeError( new IllegalArgumentException(), EntitiesInstances.VIOLATIONS_LIST )
            ).doOnError( e -> super.logging( e, EntitiesInstances.VIOLATIONS_LIST, gosno ) )
            .doOnSuccess( value -> super.logging( EntitiesInstances.VIOLATIONS_LIST.getMethodName(), value ) )
            .doOnSubscribe( value -> super.logging( EntitiesInstances.VIOLATIONS_LIST.getMethodApi() ) )
            .onErrorReturn( super.completeError( EntitiesInstances.VIOLATIONS_LIST.generate() ) );

    private final Function< String, Mono< DoverennostList > > getDoverennostList = gosno -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForGai ) )
            .get()
            .uri( EntitiesInstances.DOVERENNOST_LIST.getMethodApi() + gosno )
            .responseSingle(
                    ( res, content ) -> this.generate(
                            gosno,
                            content,
                            res,
                            this.getUpdateTokens().get().getGetDoverennostList(),
                            EntitiesInstances.DOVERENNOST_LIST
                    )
            ).retryWhen( super.retry( EntitiesInstances.DOVERENNOST_LIST ) )
            .onErrorResume(
                    throwable -> super.completeError( new ConnectTimeoutException(), EntitiesInstances.DOVERENNOST_LIST )
            ).onErrorResume(
                    throwable -> super.completeError( new IllegalArgumentException(), EntitiesInstances.DOVERENNOST_LIST )
            ).doOnError( e -> super.logging( e, EntitiesInstances.DOVERENNOST_LIST, gosno ) )
            .doOnSuccess( value -> super.logging( EntitiesInstances.DOVERENNOST_LIST.getMethodName(), value ) )
            .doOnSubscribe( value -> super.logging( EntitiesInstances.DOVERENNOST_LIST.getMethodApi() ) )
            .onErrorReturn( super.completeError( EntitiesInstances.DOVERENNOST_LIST ) );

    private final Function< String, Mono< ModelForCarList > > getModelForCarList = pinfl -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForGai ) )
            .get()
            .uri( EntitiesInstances.MODEL_FOR_CAR_LIST.getMethodApi() + pinfl )
            .responseSingle(
                    ( res, content ) -> this.generate(
                            pinfl,
                            content,
                            res,
                            this.getUpdateTokens().get().getGetModelForCarList(),
                            EntitiesInstances.MODEL_FOR_CAR_LIST
                    )
            ).retryWhen( super.retry( EntitiesInstances.MODEL_FOR_CAR_LIST ) )
            .onErrorResume(
                    throwable -> super.completeError( new ConnectTimeoutException(), EntitiesInstances.MODEL_FOR_CAR_LIST )
            ).onErrorResume(
                    throwable -> super.completeError( new IllegalArgumentException(), EntitiesInstances.MODEL_FOR_CAR_LIST )
            ).doOnError( e -> super.logging( e, EntitiesInstances.MODEL_FOR_CAR_LIST, pinfl ) )
            .doOnSuccess( value -> super.logging( EntitiesInstances.MODEL_FOR_CAR_LIST.getMethodName(), value ) )
            .doOnSubscribe( value -> super.logging( EntitiesInstances.MODEL_FOR_CAR_LIST.getMethodApi() ) )
            .onErrorReturn( super.completeError( EntitiesInstances.MODEL_FOR_CAR_LIST.generate() ) );

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
                                            EntitiesInstances.PSYCHOLOGY_CARD.generate(
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
            Config.headers.put( "Authorization", "Bearer " + token );

            psychologyCard.setForeignerList(
                    super.stringToArrayList(
                            Unirest.get(
                                    String.join(
                                            "",
                                            super.getAPI_FOR_TRAIN_TICKET_CONSUMER_SERVICE(),
                                            psychologyCard.getPapilonData().get( 0 ).getPassport()
                                    )
                            ).headers( Config.headers )
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
        } finally {
            super.close();
        }

        return super.convert( psychologyCard );
    }

    private final Function< FIO, Mono< PersonTotalDataByFIO > > getPersonTotalDataByFIO = fio -> this.getHttpClient()
            .headers( h -> h.add( "Authorization", "Bearer " + Config.tokenForFio ) )
            .post()
            .uri( super.getAPI_FOR_PERSON_DATA_FROM_ZAKS() )
            .send( this.generate( fio, new RequestForFio() ) )
            .responseSingle( ( res, content ) -> res.status().code() == 401
                    ? this.getUpdateTokens().get().getGetPersonTotalDataByFIO().apply( fio )
                    : super.checkResponse( res, content )
                            ? content
                            .asString()
                            .map( s -> {
                                final PersonTotalDataByFIO person = super.deserialize( s, PersonTotalDataByFIO.class );

                                if ( super.objectIsNotNull( person ) && super.isCollectionNotEmpty( person.getData() ) ) {
                                    super.analyze(
                                            person.getData(),
                                            person1 -> this.getGetImageByPinfl()
                                                    .apply( person1.getPinpp() )
                                                    .subscribe( person1::setPersonImage )
                                    );
                                }

                                return super.objectIsNotNull( person ) ? person : EntitiesInstances.PERSON_TOTAL_DATA_BY_FIO.generate();
                            } )
                            : super.convert( super.completeError( EntitiesInstances.PERSON_TOTAL_DATA_BY_FIO ) )
            )
            .doOnError( e -> super.logging( e, EntitiesInstances.PERSON_TOTAL_DATA_BY_FIO, fio.getName() ) )
            .onErrorReturn( super.completeError( EntitiesInstances.PERSON_TOTAL_DATA_BY_FIO ) );

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
                    : super.convert( super.completeError( EntitiesInstances.PSYCHOLOGY_CARD ) );

    private final Function< ApiResponseModel, Mono< PsychologyCard > > getPsychologyCardByPinflInitial =
            apiResponseModel -> this.checkParam( apiResponseModel.getStatus().getMessage() )
                    ? Mono.zip(
                            this.getGetPinpp().apply( apiResponseModel.getStatus().getMessage() ),
                            this.getGetImageByPinfl().apply( apiResponseModel.getStatus().getMessage() )
                    ).map( tuple -> super.saveUserUsageLog.apply( PsychologyCard.generate( tuple ), apiResponseModel ) )
                    : super.convert( super.completeError( EntitiesInstances.PSYCHOLOGY_CARD ) );

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
                    : super.convert( super.completeError( EntitiesInstances.PSYCHOLOGY_CARD ) );

    private final Function< CrossBoardInfo, Mono< CrossBoardInfo > > analyzeCrossData = crossBoardInfo ->
            super.convertValuesToParallelFluxWithMap(
                    crossBoardInfo
                            .getData()
                            .get( 0 )
                            .getCrossBoardList(),
                    crossBoard -> crossBoard.save( crossBoardInfo.getData().get( 0 ).getPerson().getNationalityid() )
            ).collectList()
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
        EntitiesInstances.SEMAPHORE.release();

        this.clean();
    }

    @Override
    public void close( final Throwable throwable ) {
        super.logging( throwable );
        this.close();
    }
}