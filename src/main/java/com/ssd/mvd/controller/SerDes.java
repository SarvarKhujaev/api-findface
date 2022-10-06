package com.ssd.mvd.controller;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.*;
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
import com.ssd.mvd.kafka.KafkaDataControl;
import com.ssd.mvd.entity.foreigner.Foreigner;
import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.entity.modelForCadastr.Data;
import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
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

    public <T> List<T> stringToArrayList ( String object, Class< T[] > clazz ) { return Arrays.asList( this.getGson().fromJson( object, clazz ) ); }

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
            log.error( e.getMessage() );
            this.updateTokens(); } }

    private void sendNotification ( String methodName, String params, String reason ) {
        this.getNotification().setPinfl( params );
        this.getNotification().setReason( reason );
        this.getNotification().setMethodName( methodName );
        this.getNotification().setCallingTime( new Date() );
        this.getNotification().setJsonNode( this.getResponse().getBody() );
        if ( this.getResponse() != null ) log.info( this.getResponse().getBody()
                + " Status: " + this.getResponse().getStatus() );
        KafkaDataControl.getInstance().writeToKafka( this.getGson().toJson( this.getNotification() ) ); }

    private Pinpp pinpp ( String pinpp ) {
        HttpResponse< JsonNode > response1;
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForPassport() );
        try { log.info( "Pinpp: " + pinpp );
            response1 = Unirest.get( this.getConfig().getAPI_FOR_PINPP() + pinpp )
                    .headers( this.getHeaders() )
                    .asJson();
            this.setResponse( response1 );
            if ( response1.getStatus() == 401 ) {
                this.updateTokens();
                return this.pinpp( pinpp ); }
            return this.getGson()
                .fromJson( response1
                        .getBody()
                        .getObject()
                        .toString(), Pinpp.class ); }
        catch ( Exception e ) {
            this.sendNotification ( "pinpp", pinpp, "Error in service: " + e.getMessage() );
            return new Pinpp(); } }

    public Data deserialize ( String pinfl ) {
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
                return this.deserialize( pinfl ); }
            JSONObject object = response1
                    .getBody()
                    .getObject();
            return object != null ? this.getGson().fromJson( object.get( "Data" ).toString(), Data.class ) : new Data();
        } catch ( JSONException | UnirestException e ) {
            this.sendNotification( "deserialize ModelForCadastr", pinfl, "Error: " + e.getMessage() );
            return new Data(); } }

    private String getImageByPinfl ( String pinpp ) {
        HttpResponse< JsonNode > response1;
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { log.info( "Pinpp: " + pinpp );
            response1 = Unirest.get( this.getConfig().getAPI_FOR_PERSON_IMAGE() + pinpp )
                    .headers( this.getHeaders() )
                    .asJson();
            this.setResponse( response1 );
            if ( response1.getStatus() == 401 ) {
                this.updateTokens();
                return getImageByPinfl( pinpp ); }
            JSONObject object = response1
                    .getBody()
                    .getObject();
            return object != null ? object.getString( "Data" ) : "image was not found";
        } catch ( JSONException | UnirestException e ) {
            this.sendNotification( "getImageByPinfl", pinpp, "Error: " + e.getMessage() );
            return "Error"; } }

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
            return this.getGson()
                    .fromJson( response1
                            .getBody()
                            .getObject()
                            .get( "Data" )
                            .toString(), com.ssd.mvd.entity.modelForPassport.Data.class ); }
        catch ( Exception e ) {
            this.sendNotification( "deserialize Passport Data",
                    SerialNumber + "_" + BirthDate,
                    "Error: " + e.getMessage() );
            return new com.ssd.mvd.entity.modelForPassport.Data(); } }

    public Insurance insurance ( String pinpp ) {
        HttpResponse< JsonNode > response1;
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { log.info( "Pinpp in insurance: " + pinpp );
            response1 = Unirest.get( this.getConfig().getAPI_FOR_FOR_INSURANCE() + pinpp )
                    .headers( this.getHeaders() )
                    .asJson();
            this.setResponse( response1 );
            if ( response1.getStatus() == 401 ) {
                this.updateTokens();
                return this.insurance( pinpp ); }
            return this.getGson()
                    .fromJson( response1
                            .getBody()
                            .getArray()
                            .get( 0 )
                            .toString(), Insurance.class );
        } catch ( Exception e ) {
            this.sendNotification( "insurance", pinpp, "Error: " + e.getMessage() );
            return new Insurance(); } }

    public ModelForCar getVehicleData ( String gosno ) {
        HttpResponse< JsonNode > response1;
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { log.info( "Gosno in getVehicleData: " + gosno );
            response1 = Unirest.get( this.getConfig().getAPI_FOR_VEHICLE_DATA() + gosno )
                    .headers( this.getHeaders() )
                    .asJson();
            this.setResponse( response1 );
            if ( response1.getStatus() == 401 ) {
                this.updateTokens();
                return this.getVehicleData( gosno ); }
            if ( this.getResponse().getStatus() == 200 ) return this.getGson()
                .fromJson( response1
                        .getBody()
                        .getArray()
                        .get( 0 )
                        .toString(), ModelForCar.class );
            else {
                this.sendNotification( "getVehicleData", gosno, "Data was not found" );
                return new ModelForCar(); }
        } catch ( Exception e ) {
            this.sendNotification( "getVehicleData", gosno, e.getMessage() );
            return new ModelForCar(); } }

    public Tonirovka getVehicleTonirovka ( String gosno ) {
        HttpResponse< JsonNode > response1;
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { log.info( "Gosno in getVehicleTonirovka: " + gosno );
            response1 = Unirest.get( this.getConfig().getAPI_FOR_TONIROVKA() + gosno )
                    .headers( this.getHeaders() )
                    .asJson();
            if ( response1.getStatus() == 401 ) {
                this.updateTokens();
                return this.getVehicleTonirovka( gosno ); }
            return this.getGson()
                .fromJson( response1
                        .getBody()
                        .toString(), Tonirovka.class );
        } catch ( Exception e ) {
            this.sendNotification( "getVehicleTonirovka", gosno, e.getMessage() );
            return new Tonirovka(); } }

    public ViolationsList getViolationList ( String gosno ) {
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { log.info( "Gosno in getViolationList: " + gosno );
            this.setResponse( Unirest.get( this.getConfig().getAPI_FOR_VIOLATION_LIST() + gosno )
                    .headers( this.getHeaders() )
                    .asJson() );
            if ( this.getResponse().getStatus() == 401 ) {
                this.updateTokens();
                return this.getViolationList( gosno ); }
            if ( this.getResponse().getStatus() == 200 ) return new ViolationsList( this.stringToArrayList(
                this.getResponse()
                        .getBody()
                        .getArray()
                        .toString(), ViolationsInformation[].class ) );

            else { this.sendNotification( "getViolationList", gosno, "Data was not found" );
                return new ViolationsList(); } }
        catch ( Exception e ) {
            this.sendNotification( "getViolationList", gosno, e.getMessage() );
            return new ViolationsList( new ArrayList<>() ); } }

    public DoverennostList getDoverennostList ( String gosno ) {
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { log.info( "Gosno in getDoverennostList: " + gosno );
            this.setResponse( Unirest.get( this.getConfig().getAPI_FOR_DOVERENNOST_LIST() + gosno )
                    .headers( this.getHeaders() )
                    .asJson() );
            if ( this.getResponse().getStatus() == 401 ) {
                this.updateTokens();
                return this.getDoverennostList( gosno ); }
            return new DoverennostList( this.stringToArrayList(
                    this.getResponse()
                            .getBody()
                            .getArray()
                            .toString(), Doverennost[].class ) ); }
        catch ( Exception e ) {
            this.sendNotification( "getDoverennostList", gosno, "Error: " + e.getMessage() );
            return new DoverennostList( new ArrayList<>() ); } }

    private ModelForCarList getModelForCarList ( String pinfl ) {
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { log.info( "Pinfl in getModelForCarList: " + pinfl );
            this.setResponse( Unirest.get( this.getConfig().getAPI_FOR_MODEL_FOR_CAR_LIST() + pinfl )
                    .headers( this.getHeaders() )
                    .asJson() );
            if ( this.getResponse().getStatus() == 401 ) {
                this.updateTokens();
                return this.getModelForCarList( pinfl ); }
            if ( this.getResponse().getStatus() == 200 ) return new ModelForCarList( this.stringToArrayList(
                    this.getResponse()
                            .getBody()
                            .getArray()
                            .toString(), ModelForCar[].class ) );

            else { this.sendNotification( "getModelForCarList", pinfl, "Data was not found" );
                return new ModelForCarList(); } }
        catch ( Exception e ) {
            this.sendNotification( "getModelForCarList", pinfl, "Error: " + e.getMessage() );
            return new ModelForCarList(); } }

    private ModelForAddress getModelForAddress ( String pinfl ) {
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
                return this.getModelForAddress( pinfl ); }
            return this.getGson()
                    .fromJson( this.getResponse()
                            .getBody()
                            .getObject()
                            .get( "Data" )
                            .toString(), ModelForAddress.class ); }
        catch ( Exception e ) {
            this.sendNotification( "getModelForAddress", pinfl, "Error: " + e.getMessage() );
            return new ModelForAddress(); } }

    private void findAllDataAboutCar ( PsychologyCard psychologyCard ) {
        if ( psychologyCard.getModelForCarList() != null
                && psychologyCard
                .getModelForCarList()
                .getModelForCarList() != null
                && psychologyCard
                .getModelForCarList()
                .getModelForCarList()
                .size() > 0 ) psychologyCard
                .getModelForCarList()
                .getModelForCarList()
                .forEach( modelForCar -> {
            modelForCar.setInsurance( this.insurance( modelForCar.getPlateNumber() ) );
            modelForCar.setTonirovka( this.getVehicleTonirovka( modelForCar.getPlateNumber() ) );
            modelForCar.setDoverennostList( this.getDoverennostList( modelForCar.getPlateNumber() ) ); } ); }

    private void setPersonPrivateData ( PsychologyCard psychologyCard ) {
        psychologyCard.setModelForCadastr( this.deserialize( psychologyCard.getPinpp().getCadastre() ) );
        if ( psychologyCard.getModelForCadastr() != null
                && psychologyCard
                .getModelForCadastr()
                .getPermanentRegistration() != null
                && psychologyCard
                .getModelForCadastr()
                .getPermanentRegistration().size() > 0 ) psychologyCard
                .getModelForCadastr()
                .getPermanentRegistration()
                .forEach( person -> {
                    if ( person
                            .getPDateBirth()
                            .equals( psychologyCard
                                    .getPinpp()
                                    .getBirthDate() ) ) {
                        psychologyCard.setModelForPassport (
                                this.deserialize (
                                        person.getPPsp(),
                                        person.getPDateBirth() ) );
                        psychologyCard.setModelForAddress(
                                this.getModelForAddress( person.getPCitizen() ) ); } } ); }

    private void setFamilyData ( Results results, PsychologyCard psychologyCard ) {
        // личные данные человека чьи данные были переданы на данный сервис
        psychologyCard.setChildData( results.getChildData() );

        // личные данные матери, того чьи данные были переданы на данный сервис
        psychologyCard.setMommyData( results.getMommyData() );
        psychologyCard.setMommyPinfl( results.getMommyPinfl() );

        // личные данные отца, того чьи данные были переданы на данный сервис
        psychologyCard.setDaddyData( results.getDaddyData() );
        psychologyCard.setDaddyPinfl( results.getDaddyPinfl() );

        if ( psychologyCard.getChildData() != null
                && psychologyCard.getChildData().getItems() != null
                && !psychologyCard.getChildData().getItems().isEmpty()
                && psychologyCard.getChildData().getItems().size() > 0 ) psychologyCard
                    .getChildData()
                    .getItems()
                    .forEach( familyMember -> familyMember
                            .setPersonal_image( this.getImageByPinfl( familyMember.getPnfl() ) ) );

        if ( psychologyCard.getDaddyData() != null
                && psychologyCard.getDaddyData().getItems() != null
                && !psychologyCard.getDaddyData().getItems().isEmpty()
                && psychologyCard.getDaddyData().getItems().size() > 0 ) psychologyCard
                    .getDaddyData()
                    .getItems()
                    .forEach( familyMember -> familyMember
                            .setPersonal_image( this.getImageByPinfl( familyMember.getPnfl() ) ) );

        if ( psychologyCard.getMommyData() != null
                && psychologyCard.getMommyData().getItems() != null
                && !psychologyCard.getMommyData().getItems().isEmpty()
                && psychologyCard.getMommyData().getItems().size() > 0 ) psychologyCard
                .getMommyData()
                .getItems()
                .forEach( familyMember -> familyMember
                        .setPersonal_image( this.getImageByPinfl( familyMember.getPnfl() ) ) ); }

    public Mono< PersonTotalDataByFIO > getPersonTotalDataByFIO ( FIO fio ) {
        if ( fio.getSurname() == null
                ^ fio.getName() == null
                && fio.getPatronym() == null ) return Mono.just( new PersonTotalDataByFIO() );
        this.getFields().clear();
        HttpResponse< String > response1;
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForFio() );
        this.getFields().put( "Surname", fio.getSurname().toUpperCase( Locale.ROOT ) );
        this.getFields().put( "Name", fio.getName() != null ? fio.getName().toUpperCase( Locale.ROOT ) : null );
        this.getFields().put( "Patronym", fio.getPatronym() != null ? fio.getPatronym().toUpperCase( Locale.ROOT ) : null );
        try { response1 = Unirest.post( this.getConfig().getAPI_FOR_PERSON_DATA_FROM_ZAKS() )
                .headers( this.getHeaders() )
                .fields( this.getFields() )
                .asString();
            if ( response1.getStatus() == 401 ) {
                this.updateTokens();
                return this.getPersonTotalDataByFIO( fio ); }
            PersonTotalDataByFIO person = this.getGson()
                    .fromJson( response1.getBody(), PersonTotalDataByFIO.class );
            if ( person != null && person.getData().size() > 0 ) {
                person.getData()
                        .forEach( person1 -> person1.setPersonImage( this.getImageByPinfl( person1.getPinpp() ) ) );
                Mono.just( new UserRequest( person, fio ) )
                        .subscribe( userRequest -> KafkaDataControl
                                .getInstance()
                                .writeToKafkaServiceUsage( this.getGson().toJson( userRequest ) ) ); }
            return Mono.just( person != null ? person : new PersonTotalDataByFIO() );
        } catch ( Exception e ) { return Mono.just( new PersonTotalDataByFIO() ); } }

    public PsychologyCard getPsychologyCard ( ApiResponseModel apiResponseModel ) {
        if ( apiResponseModel.getStatus().getMessage() == null ) return null;
        PsychologyCard psychologyCard = new PsychologyCard();
        try { FindFaceComponent
                    .getInstance()
                    .getViolationListByPinfl( apiResponseModel.getStatus().getMessage() )
                    .doOnError( throwable -> {
                        log.error( "ERROR before sending request: " + throwable.getCause() );
                        log.error( "ERROR before sending request: " + throwable.getMessage() ); } )
                    .subscribe( list -> psychologyCard.setViolationList( list != null ? list : new ArrayList<>() ) );
        } catch ( Exception e ) { psychologyCard.setViolationList( new ArrayList<>() ); }

        try {
            log.info( "Pinfl before: " + apiResponseModel.getStatus().getMessage() );
            FindFaceComponent
                    .getInstance()
                    .getFamilyMembersData( apiResponseModel.getStatus().getMessage() )
                    .subscribe( results -> this.setFamilyData( results, psychologyCard ) );
        } catch ( Exception e ) {
            log.error( "Error while getting family members" );
            psychologyCard.setDaddyData( null );
            psychologyCard.setMommyData( null );
            psychologyCard.setChildData( null ); }

        psychologyCard.setPinpp( this.pinpp( apiResponseModel.getStatus().getMessage() ) );
        psychologyCard.setPersonImage( this.getImageByPinfl( apiResponseModel.getStatus().getMessage() ) );
        psychologyCard.setModelForCarList( this.getModelForCarList( apiResponseModel.getStatus().getMessage() ) );
        this.findAllDataAboutCar( psychologyCard );
        this.setPersonPrivateData( psychologyCard );
        Mono.just( new UserRequest( psychologyCard, apiResponseModel ) )
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
                    .subscribe( userRequest -> KafkaDataControl
                            .getInstance()
                            .writeToKafkaServiceUsage( this.getGson().toJson( userRequest ) ) );
        } catch ( Exception e ) {
            this.sendNotification( "getPsychologyCard",
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
            psychologyCard.setPinpp( this.pinpp( results
                            .getResults()
                            .get( 0 )
                            .getPersonal_code() ) );
            psychologyCard.setModelForCadastr( this.deserialize(
                    psychologyCard
                            .getPinpp()
                            .getCadastre() ) );
            psychologyCard.setPersonImage( this.getImageByPinfl( results
                    .getResults()
                    .get( 0 )
                    .getPersonal_code() ) );
            psychologyCard.setModelForCarList( this.getModelForCarList(
                    results
                            .getResults()
                            .get( 0 )
                            .getPersonal_code() ) );
            this.findAllDataAboutCar( psychologyCard );
            this.setPersonPrivateData( psychologyCard );
            Mono.just( new UserRequest( psychologyCard, apiResponseModel ) )
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
        try { FindFaceComponent
                    .getInstance()
                    .getViolationListByPinfl( data.getPerson().getPinpp() )
                    .subscribe( value -> psychologyCard.setViolationList( value != null ? value : new ArrayList<>() ) );
        } catch ( Exception e ) { psychologyCard.setViolationList( new ArrayList<>() ); }

        try { FindFaceComponent
                .getInstance()
                .getFamilyMembersData( data.getPerson().getPinpp() )
                .defaultIfEmpty( new Results() )
                .subscribe( results -> this.setFamilyData( results, psychologyCard ) );
        } catch ( Exception e ) {
            psychologyCard.setDaddyData( null );
            psychologyCard.setMommyData( null );
            psychologyCard.setChildData( null ); }

        psychologyCard.setPinpp( this.pinpp( data.getPerson().getPinpp() ) );
        psychologyCard.setPersonImage( this.getImageByPinfl( data.getPerson().getPinpp() ) );
        psychologyCard.setModelForCarList( this.getModelForCarList( data.getPerson().getPinpp() ) );
        psychologyCard.setModelForAddress( this.getModelForAddress( data.getPerson().getPCitizen() ) );
        psychologyCard.setModelForCadastr( this.deserialize( psychologyCard.getPinpp().getCadastre() ) );
        this.findAllDataAboutCar( psychologyCard );
        Mono.just( new UserRequest( psychologyCard, apiResponseModel ) )
                .subscribe( userRequest -> KafkaDataControl
                        .getInstance()
                        .writeToKafkaServiceUsage( this.getGson().toJson( userRequest ) ) );
        return psychologyCard; }

    @Override
    public void run () {
        while ( true ) {
            this.updateTokens();
            try { Thread.sleep( 60 * 60 * 1000 ); } catch ( InterruptedException e ) { e.printStackTrace(); } } }
}