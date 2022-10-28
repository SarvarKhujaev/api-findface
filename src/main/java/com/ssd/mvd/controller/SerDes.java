package com.ssd.mvd.controller;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import com.google.gson.Gson;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.ssd.mvd.entity.*;
import com.ssd.mvd.kafka.Notification;
import com.ssd.mvd.entity.modelForGai.*;
import com.ssd.mvd.entity.family.Family;
import com.ssd.mvd.kafka.KafkaDataControl;
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
    private String tokenForGai;
    private String tokenForFio;
    private String tokenForPassport;

    private final Gson gson = new Gson();
    private final Config config = new Config();
    private static SerDes serDes = new SerDes();

    private HttpResponse< JsonNode > response;
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
        this.getHeaders().put( "accept", "application/json" );
        this.updateTokens(); }

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
        } catch ( UnirestException e ) {
            this.sendErrorLog( e.getMessage(),
                    IntegratedServiceApis.OVIR.getName(),
                    IntegratedServiceApis.OVIR.getDescription() );
            log.error( e.getMessage() );
            this.updateTokens(); } }

    private final Function< String, String > base64ToLink = base64 -> {
        this.getFields().clear();
        HttpResponse< JsonNode > response;
        this.getFields().put( "photo", base64 );
        this.getFields().put( "serviceName", "psychologyCard" );
        try { log.info( "Converting image to Link in: base64ToLink method"  );
            response = Unirest.post( this.getConfig().getBASE64_IMAGE_TO_LINK_CONVERTER_API() )
                    .fields( this.getFields() )
                    .asJson();
            return response.getStatus() == 200
                    ? response
                    .getBody()
                    .getObject()
                    .get( "Data" )
                    .toString()
                    : "not found"; }
        catch ( UnirestException e ) {
            this.sendErrorLog( "base64ToLink", "base64ToLink", "Error: " + e.getMessage() );
            return "error"; } };

    private void sendErrorLog ( String methodName,
                                String params,
                                String reason ) {
        this.getNotification().setPinfl( params );
        this.getNotification().setReason( reason );
        this.getNotification().setMethodName( methodName );
        this.getNotification().setCallingTime( new Date() );
        this.getNotification().setJsonNode( this.getResponse().getBody() );
        if ( this.getResponse() != null ) log.info( this.getResponse().getBody()
                + " Status: " + this.getResponse().getStatus() );
        KafkaDataControl.getInstance().writeToKafka( this.getGson().toJson( this.getNotification() ) ); }

    private void saveErrorLog ( String errorMessage,
                                String integratedService,
                                String integratedServiceDescription ) {
        KafkaDataControl
                .getInstance()
                .writeToKafkaErrorLog( this.getGson()
                        .toJson( ErrorLog
                                .builder()
                                .errorMessage( errorMessage )
                                .createdAt( new Date().getTime() )
                                .integratedService( integratedService )
                                .integratedServiceApiDescription( integratedServiceDescription )
                                .build() ) ); }

    private final Function< String, Pinpp > pinpp = pinpp -> {
        HttpResponse< JsonNode > response1;
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForPassport() );
        try { log.info( "Pinpp: " + pinpp );
            response1 = Unirest.get( this.getConfig().getAPI_FOR_PINPP() + pinpp )
                    .headers( this.getHeaders() )
                    .asJson();
            this.setResponse( response1 );
            if ( response1.getStatus() == 401 ) {
                this.updateTokens();
                return this.getPinpp().apply( pinpp ); }

            if ( this.check500Error.test( this.getResponse() ) ) this.saveErrorLog( this.getResponse()
                            .getStatusText(),
                    IntegratedServiceApis.OVIR.getName(),
                    IntegratedServiceApis.OVIR.getDescription() );

            return this.getGson()
                    .fromJson( response1
                            .getBody()
                            .getObject()
                            .toString(), Pinpp.class ); }
        catch ( Exception e ) {
            this.saveErrorLog( e.getMessage(),
                    IntegratedServiceApis.OVIR.getName(),
                    IntegratedServiceApis.OVIR.getDescription() );
            this.sendErrorLog( "pinpp", pinpp, "Error in service: " + e.getMessage() );
            return new Pinpp(); } };

    private final Function< String, Data > deserialize = pinfl -> {
        this.getFields().clear();
        HttpResponse< JsonNode > response1;
        this.getFields().put( "Pcadastre", pinfl );
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForPassport() );
        try {  log.info( "Pcadastre in deserialize 310: " + pinfl );
            response1 = Unirest.post( this.getConfig().getAPI_FOR_CADASTR() )
                    .headers( this.getHeaders() )
                    .fields( this.getFields() )
                    .asJson();
            if ( response1.getStatus() == 401 ) {
                this.updateTokens();
                return this.getDeserialize().apply( pinfl ); }

            if ( this.check500Error.test( this.getResponse() ) ) this.saveErrorLog(
                            this.getResponse().getStatusText(),
                            IntegratedServiceApis.OVIR.getName(),
                            IntegratedServiceApis.OVIR.getDescription() );

            JSONObject object = response1
                    .getBody()
                    .getObject();
            return object != null ? this.getGson().fromJson( object.get( "Data" ).toString(), Data.class ) : new Data();
        } catch ( JSONException | UnirestException e ) {
            this.saveErrorLog( e.getMessage(),
                    IntegratedServiceApis.OVIR.getName(),
                    IntegratedServiceApis.OVIR.getDescription() );
            this.sendErrorLog( "deserialize ModelForCadastr", pinfl, "Error: " + e.getMessage() );
            return new Data(); } };

    private final Function< String, String > getImageByPinfl = pinpp -> {
        HttpResponse< JsonNode > response1;
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { log.info( "Pinpp: " + pinpp );
            response1 = Unirest.get( this.getConfig().getAPI_FOR_PERSON_IMAGE() + pinpp )
                    .headers( this.getHeaders() )
                    .asJson();
            this.setResponse( response1 );
            if ( response1.getStatus() == 401 ) {
                this.updateTokens();
                return getGetImageByPinfl().apply( pinpp ); }

            if ( this.check500Error.test( this.getResponse() ) ) this.saveErrorLog(
                    this.getResponse().getStatusText(),
                    IntegratedServiceApis.OVIR.getName(),
                    IntegratedServiceApis.OVIR.getDescription() );

            JSONObject object = response1
                    .getBody()
                    .getObject();
            return object != null ? object.getString( "Data" ) : "image was not found";
        } catch ( JSONException | UnirestException e ) {
            this.saveErrorLog( e.getMessage(),
                    IntegratedServiceApis.OVIR.getName(),
                    IntegratedServiceApis.OVIR.getDescription() );
            this.sendErrorLog( "getImageByPinfl", pinpp, "Error: " + e.getMessage() );
            return "Error"; } };

    private final Function< String, ModelForAddress > getModelForAddress = pinfl -> {
        try { log.info( "Pinfl in getModelForAddress: " + pinfl );
            this.getFields().clear();
            this.getFields().put( "Pcitizen", pinfl );
            this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
            this.setResponse( Unirest.post( this.getConfig().getAPI_FOR_MODEL_FOR_ADDRESS() )
                    .headers( this.getHeaders() )
                    .field( "Pcitizen", pinfl )
                    .asJson() );
            if ( this.getResponse().getStatus() == 401 ) {
                this.updateTokens();
                return this.getGetModelForAddress().apply( pinfl ); }
            if ( this.check500Error.test( this.getResponse() ) ) this.saveErrorLog(
                    this.getResponse().getStatusText(),
                    IntegratedServiceApis.OVIR.getName(),
                    IntegratedServiceApis.OVIR.getDescription() );
            return this.getGson()
                    .fromJson( this.getResponse()
                            .getBody()
                            .getObject()
                            .get( "Data" )
                            .toString(), ModelForAddress.class ); }
        catch ( Exception e ) {
            this.saveErrorLog( e.getMessage(),
                    IntegratedServiceApis.OVIR.getName(),
                    IntegratedServiceApis.OVIR.getDescription() );
            this.sendErrorLog( "getModelForAddress", pinfl, "Error: " + e.getMessage() );
            return new ModelForAddress(); } };

    public com.ssd.mvd.entity.modelForPassport.Data deserialize ( String SerialNumber, String BirthDate ) {
        this.getFields().clear();
        HttpResponse< JsonNode > response1;
        this.getFields().put( "BirthDate", BirthDate );
        this.getFields().put( "SerialNumber", SerialNumber );
        log.info( "Data: " + SerialNumber + " : " + BirthDate );
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForPassport() );
        try { response1 = Unirest.post( this.getConfig().getAPI_FOR_PASSPORT_MODEL() )
                .headers( this.getHeaders() )
                .fields( this.getFields() )
                .asJson();
            this.setResponse( response1 );
            if ( response1.getStatus() == 401 ) {
                this.updateTokens();
                return this.deserialize( SerialNumber, BirthDate ); }

            if ( this.check500Error.test( this.getResponse() ) ) this.saveErrorLog(
                    this.getResponse().getStatusText(),
                    IntegratedServiceApis.OVIR.getName(),
                    IntegratedServiceApis.OVIR.getDescription() );

            return this.getGson()
                    .fromJson( response1
                            .getBody()
                            .getObject()
                            .get( "Data" )
                            .toString(), com.ssd.mvd.entity.modelForPassport.Data.class ); }
        catch ( Exception e ) {
            this.saveErrorLog( e.getMessage(),
                    IntegratedServiceApis.OVIR.getName(),
                    IntegratedServiceApis.OVIR.getDescription() );
            this.sendErrorLog( "deserialize Passport Data",
                    SerialNumber + "_" + BirthDate,
                    "Error: " + e.getMessage() );
            return new com.ssd.mvd.entity.modelForPassport.Data(); } }

    private final Function< String, Insurance > insurance = pinpp -> {
        HttpResponse< JsonNode > response1;
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { log.info( "Pinpp in insurance: " + pinpp );
            response1 = Unirest.get( this.getConfig().getAPI_FOR_FOR_INSURANCE() + pinpp )
                    .headers( this.getHeaders() )
                    .asJson();
            if ( response1.getStatus() == 401 ) {
                this.updateTokens();
                return this.insurance.apply( pinpp ); }

            if ( this.check500Error.test( response1 ) ) this.saveErrorLog( response1.getStatusText(),
                    IntegratedServiceApis.GAI.getName(),
                    IntegratedServiceApis.GAI.getDescription() );

            return this.getGson()
                    .fromJson( response1
                            .getBody()
                            .getArray()
                            .get( 0 )
                            .toString(), Insurance.class );
        } catch ( Exception e ) {
            this.saveErrorLog( e.getMessage(),
                    IntegratedServiceApis.GAI.getName(),
                    IntegratedServiceApis.GAI.getDescription() );
            this.sendErrorLog( "insurance", pinpp, "Error: " + e.getMessage() );
            return new Insurance(); } };

    private final Function< String, ModelForCar > getVehicleData = gosno -> {
        HttpResponse< JsonNode > response1;
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { log.info( "Gosno in getVehicleData: " + gosno );
            response1 = Unirest.get( this.getConfig().getAPI_FOR_VEHICLE_DATA() + gosno )
                    .headers( this.getHeaders() )
                    .asJson();
            this.setResponse( response1 );
            if ( response1.getStatus() == 401 ) {
                this.updateTokens();
                return this.getVehicleData.apply( gosno ); }

            if ( this.check500Error.test( response1 ) ) this.saveErrorLog(
                    response1.getStatusText(),
                    IntegratedServiceApis.GAI.getName(),
                    IntegratedServiceApis.GAI.getDescription() );

            if ( this.getResponse().getStatus() == 200 ) return this.getGson()
                    .fromJson( response1
                            .getBody()
                            .getArray()
                            .get( 0 )
                            .toString(), ModelForCar.class );
            else {
                this.sendErrorLog( "getVehicleData", gosno, "Data was not found" );
                return new ModelForCar(); }
        } catch ( Exception e ) {
            this.saveErrorLog( e.getMessage(),
                    IntegratedServiceApis.GAI.getName(),
                    IntegratedServiceApis.GAI.getDescription() );
            this.sendErrorLog( "getVehicleData", gosno, e.getMessage() );
            return new ModelForCar(); } };

    private final Function< String, Tonirovka > getVehicleTonirovka = gosno -> {
        HttpResponse< JsonNode > response1;
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { log.info( "Gosno in getVehicleTonirovka: " + gosno );
            response1 = Unirest.get( this.getConfig().getAPI_FOR_TONIROVKA() + gosno )
                    .headers( this.getHeaders() )
                    .asJson();
            if ( response1.getStatus() == 401 ) {
                this.updateTokens();
                return this.getVehicleTonirovka.apply( gosno ); }

            if ( this.check500Error.test( response1 ) ) this.saveErrorLog(
                    response1.getStatusText(),
                    IntegratedServiceApis.GAI.getName(),
                    IntegratedServiceApis.GAI.getDescription() );

            return this.getGson()
                    .fromJson( response1
                            .getBody()
                            .toString(), Tonirovka.class );
        } catch ( Exception e ) {
            this.saveErrorLog( e.getMessage(),
                    IntegratedServiceApis.GAI.getName(),
                    IntegratedServiceApis.GAI.getDescription() );
            this.sendErrorLog( "getVehicleTonirovka", gosno, e.getMessage() );
            return new Tonirovka(); } };

    private final Function< String, ViolationsList > getViolationList = gosno -> {
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { log.info( "Gosno in getViolationList: " + gosno );
            this.setResponse( Unirest.get( this.getConfig().getAPI_FOR_VIOLATION_LIST() + gosno )
                    .headers( this.getHeaders() )
                    .asJson() );
            if ( this.getResponse().getStatus() == 401 ) {
                this.updateTokens();
                return this.getViolationList.apply( gosno ); }

            if ( this.check500Error.test( this.getResponse() ) ) this.saveErrorLog(
                    this.getResponse().getStatusText(),
                    IntegratedServiceApis.GAI.getName(),
                    IntegratedServiceApis.GAI.getDescription() );

            if ( this.getResponse().getStatus() == 200 ) return new ViolationsList( this.stringToArrayList(
                    this.getResponse()
                            .getBody()
                            .getArray()
                            .toString(), ViolationsInformation[].class ) );

            else { this.sendErrorLog( "getViolationList", gosno, "Data was not found" );
                return new ViolationsList(); } }
        catch ( Exception e ) {
            this.saveErrorLog( e.getMessage(),
                    IntegratedServiceApis.GAI.getName(),
                    IntegratedServiceApis.GAI.getDescription() );
            this.sendErrorLog( "getViolationList", gosno, e.getMessage() );
            return new ViolationsList( new ArrayList<>() ); } };

    private final Function< String, DoverennostList > getDoverennostList = gosno -> {
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { log.info( "Gosno in getDoverennostList: " + gosno );
            this.setResponse( Unirest.get( this.getConfig().getAPI_FOR_DOVERENNOST_LIST() + gosno )
                    .headers( this.getHeaders() )
                    .asJson() );
            if ( this.getResponse().getStatus() == 401 ) {
                this.updateTokens();
                return this.getDoverennostList.apply( gosno ); }

            if ( this.check500Error.test( this.getResponse() ) ) this.saveErrorLog(
                    this.getResponse().getStatusText(),
                    IntegratedServiceApis.GAI.getName(),
                    IntegratedServiceApis.GAI.getDescription() );

            return new DoverennostList( this.stringToArrayList(
                    this.getResponse()
                            .getBody()
                            .getArray()
                            .toString(), Doverennost[].class ) ); }
        catch ( Exception e ) {
            this.saveErrorLog( e.getMessage(),
                    IntegratedServiceApis.GAI.getName(),
                    IntegratedServiceApis.GAI.getDescription() );
            this.sendErrorLog( "getDoverennostList", gosno, "Error: " + e.getMessage() );
            return new DoverennostList( new ArrayList<>() ); } };

    private final Predicate< HttpResponse< ? > > check500Error = response ->
            response.getStatus() == 500
            ^ response.getStatus() == 501
            ^ response.getStatus() == 502
            ^ response.getStatus() == 503;

    private final Function< String, ModelForCarList > getModelForCarList = pinfl -> {
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { log.info( "Pinfl in getModelForCarList: " + pinfl );
            this.setResponse( Unirest.get( this.getConfig().getAPI_FOR_MODEL_FOR_CAR_LIST() + pinfl )
                    .headers( this.getHeaders() )
                    .asJson() );
            if ( this.getResponse().getStatus() == 401 ) {
                this.updateTokens();
                return this.getModelForCarList.apply( pinfl ); }

            if ( this.check500Error.test( this.getResponse() ) ) this.saveErrorLog(
                    this.getResponse().getStatusText(),
                    IntegratedServiceApis.GAI.getName(),
                    IntegratedServiceApis.GAI.getDescription() );

            if ( this.getResponse().getStatus() == 200 ) return new ModelForCarList( this.stringToArrayList(
                    this.getResponse()
                            .getBody()
                            .getArray()
                            .toString(), ModelForCar[].class ) );

            else { this.sendErrorLog( "getModelForCarList", pinfl, "Data was not found" );
                return new ModelForCarList(); } }
        catch ( Exception e ) {
            this.saveErrorLog( e.getMessage(),
                    IntegratedServiceApis.GAI.getName(),
                    IntegratedServiceApis.GAI.getDescription() );
            this.sendErrorLog( "getModelForCarList", pinfl, "Error: " + e.getMessage() );
            return new ModelForCarList(); } };

    private final Predicate< PsychologyCard > checkCarData = psychologyCard -> psychologyCard.getModelForCarList() != null
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
                .forEach( modelForCar -> {
                    modelForCar.setInsurance( this.insurance.apply( modelForCar.getPlateNumber() ) );
                    modelForCar.setTonirovka( this.getVehicleTonirovka.apply( modelForCar.getPlateNumber() ) );
                    modelForCar.setDoverennostList( this.getDoverennostList.apply( modelForCar.getPlateNumber() ) ); } ); };

    private final Predicate< PsychologyCard > checkPrivateData = psychologyCard -> psychologyCard.getModelForCadastr() != null
            && psychologyCard
            .getModelForCadastr()
            .getPermanentRegistration() != null
            && psychologyCard
            .getModelForCadastr()
            .getPermanentRegistration().size() > 0;

    private final Consumer< PsychologyCard > setPersonPrivateData = psychologyCard -> {
        psychologyCard.setModelForCadastr( this.getDeserialize()
                .apply( psychologyCard.getPinpp().getCadastre() ) );
        if ( this.checkPrivateData.test( psychologyCard ) ) psychologyCard
                .getModelForCadastr()
                .getPermanentRegistration()
                .forEach( person -> {
                    if ( person
                            .getPDateBirth()
                            .equals( psychologyCard
                                    .getPinpp()
                                    .getBirthDate() ) ) {
                        psychologyCard.setModelForPassport (
                                this.deserialize ( person.getPPsp(),
                                        person.getPDateBirth() ) );
                        psychologyCard.setModelForAddress(
                                this.getGetModelForAddress().apply( person.getPCitizen() ) ); } } ); };

    private final Predicate< Family > checkFamily = family -> family != null
            && family.getItems() != null
            && !family.getItems().isEmpty()
            && family.getItems().size() > 0;

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
                    .forEach( familyMember -> familyMember
                            .setPersonal_image( this.getGetImageByPinfl()
                                    .apply( familyMember.getPnfl() ) ) );

        if ( this.checkFamily.test( psychologyCard.getDaddyData() ) ) psychologyCard
                    .getDaddyData()
                    .getItems()
                    .forEach( familyMember -> familyMember
                            .setPersonal_image( this.getGetImageByPinfl()
                                    .apply( familyMember.getPnfl() ) ) );

        if ( this.checkFamily.test( psychologyCard.getMommyData() ) ) psychologyCard
                .getMommyData()
                .getItems()
                .forEach( familyMember -> familyMember
                        .setPersonal_image( this.getGetImageByPinfl()
                                .apply( familyMember.getPnfl() ) ) ); }

    public Mono< PersonTotalDataByFIO > getPersonTotalDataByFIO ( FIO fio ) {
        if ( fio.getSurname() == null
                && fio.getName() == null
                && fio.getPatronym() == null ) return Mono.just( new PersonTotalDataByFIO() );
        this.getFields().clear();
        HttpResponse< String > httpResponse;
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForFio() );
        this.getFields().put( "Name", fio.getName() != null ? fio.getName().toUpperCase( Locale.ROOT ) : null );
        this.getFields().put( "Surname", fio.getSurname() != null ? fio.getSurname().toUpperCase( Locale.ROOT ) : null );
        this.getFields().put( "Patronym", fio.getPatronym() != null ? fio.getPatronym().toUpperCase( Locale.ROOT ) : null );
        try { httpResponse = Unirest.post( this.getConfig().getAPI_FOR_PERSON_DATA_FROM_ZAKS() )
                .headers( this.getHeaders() )
                .fields( this.getFields() )
                .asString();
            if ( httpResponse.getStatus() == 401 ) {
                this.updateTokens();
                return this.getPersonTotalDataByFIO( fio ); }

            if ( this.check500Error.test( httpResponse ) ) this.saveErrorLog(
                    httpResponse.getStatusText(),
                    IntegratedServiceApis.GAI.getName(),
                    IntegratedServiceApis.GAI.getDescription() );

            PersonTotalDataByFIO person = this.getGson()
                    .fromJson( httpResponse.getBody(),
                            PersonTotalDataByFIO.class );
            if ( person != null && person.getData().size() > 0 ) {
                person.getData()
                        .forEach( person1 -> person1.setPersonImage( this.getGetImageByPinfl()
                                .apply( person1.getPinpp() ) ) );
                Mono.just( new UserRequest( person, fio ) )
                        .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                                error.getMessage(), object ) ) )
                        .subscribe( userRequest -> KafkaDataControl
                                .getInstance()
                                .writeToKafkaServiceUsage( this.getGson().toJson( userRequest ) ) ); }
            return Mono.just( person != null ? person : new PersonTotalDataByFIO() );
        } catch ( Exception e ) {
            this.saveErrorLog( e.getMessage(),
                    IntegratedServiceApis.GAI.getName(),
                    IntegratedServiceApis.GAI.getDescription() );
            this.sendErrorLog( "getPersonTotalDataByFIO", fio.getName(), "Error: " + e.getMessage() );
            return Mono.just( new PersonTotalDataByFIO() ); } }

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

        psychologyCard.setPinpp( this.getPinpp().apply( apiResponseModel.getStatus().getMessage() ) );
        psychologyCard.setModelForCarList( this.getModelForCarList.apply( apiResponseModel.getStatus().getMessage() ) );
        psychologyCard.setPersonImage( this.getGetImageByPinfl().apply( apiResponseModel.getStatus().getMessage() ) );
        this.setPersonPrivateData.accept( psychologyCard );
        this.findAllDataAboutCar.accept( psychologyCard );
        Mono.just( new UserRequest( psychologyCard, apiResponseModel ) )
                .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                        error.getMessage(), object ) ) )
                .subscribe( userRequest -> KafkaDataControl
                        .getInstance()
                        .writeToKafkaServiceUsage( this.getGson().toJson( userRequest ) ) );
        return psychologyCard; }

    public PsychologyCard getPsychologyCard ( PsychologyCard psychologyCard,
                                              String token,
                                              ApiResponseModel apiResponseModel ) {
        try { this.getHeaders().put( "Authorization", "Bearer " + token );
            psychologyCard.setForeignerList( this.stringToArrayList( Unirest.get( this.getConfig()
                            .getAPI_FOR_TRAIN_TICKET_CONSUMER_SERVICE() +
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
            Mono.just( new UserRequest( psychologyCard, apiResponseModel ) )
                    .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                            error.getMessage(), object ) ) )
                    .subscribe( userRequest -> KafkaDataControl
                            .getInstance()
                            .writeToKafkaServiceUsage( this.getGson().toJson( userRequest ) ) );
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
        try { this.setFamilyData( results, psychologyCard );
            psychologyCard.setPapilonData( results.getResults() );
            psychologyCard.setViolationList( results.getViolationList() );
            psychologyCard.setPinpp( this.getPinpp()
                    .apply( results
                            .getResults()
                            .get( 0 )
                            .getPersonal_code() ) );
            psychologyCard.setModelForCadastr( this.getDeserialize()
                    .apply( psychologyCard
                            .getPinpp()
                            .getCadastre() ) );
            psychologyCard.setPersonImage( this.getGetImageByPinfl()
                    .apply( results
                        .getResults()
                        .get( 0 )
                        .getPersonal_code() ) );
            psychologyCard.setModelForCarList( this.getModelForCarList.apply(
                    results
                            .getResults()
                            .get( 0 )
                            .getPersonal_code() ) );
            this.findAllDataAboutCar.accept( psychologyCard );
            this.setPersonPrivateData.accept( psychologyCard );
            Mono.just( new UserRequest( psychologyCard, apiResponseModel ) )
                    .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                            error.getMessage(), object ) ) )
                    .subscribe( userRequest -> KafkaDataControl
                            .getInstance()
                            .writeToKafkaServiceUsage( this.getGson().toJson( userRequest ) ) );
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

        psychologyCard.setPinpp( this.getPinpp().apply( data.getPerson().getPinpp() ) );
        psychologyCard.setPersonImage( this.getGetImageByPinfl()
                .apply( data.getPerson().getPinpp() ) );
        psychologyCard.setModelForCarList( this.getModelForCarList.apply( data.getPerson().getPinpp() ) );
        psychologyCard.setModelForAddress( this.getGetModelForAddress()
                .apply( data.getPerson().getPCitizen() ) );
        psychologyCard.setModelForCadastr( this.getDeserialize()
                .apply( psychologyCard.getPinpp().getCadastre() ) );
        this.findAllDataAboutCar.accept( psychologyCard );
        Mono.just( new UserRequest( psychologyCard, apiResponseModel ) )
                .onErrorContinue( ( (error, object) -> log.error( "Error: {} and reason: {}: ",
                        error.getMessage(), object ) ) )
                .subscribe( userRequest -> KafkaDataControl
                        .getInstance()
                        .writeToKafkaServiceUsage( this.getGson().toJson( userRequest ) ) );
        return psychologyCard; }

    @Override
    public void run () {
        while ( serDes != null ) {
            this.updateTokens();
            try { TimeUnit.HOURS.sleep( 3 ); } catch ( InterruptedException e ) { e.printStackTrace(); } } }
}