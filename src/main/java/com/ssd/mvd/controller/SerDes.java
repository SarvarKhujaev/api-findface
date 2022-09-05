package com.ssd.mvd.controller;

import com.google.gson.Gson;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.ssd.mvd.entity.*;
import com.ssd.mvd.entity.modelForGai.*;
import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.entity.modelForCadastr.Data;
import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.modelForFioOfPerson.PersonTotalDataByFIO;

import java.util.*;
import org.json.JSONObject;
import org.json.JSONException;
import reactor.core.publisher.Mono;

@lombok.Data
public class SerDes implements Runnable {
    private final Gson gson = new Gson();
    private static SerDes serDes = new SerDes();

    private final Map< String, Object > fields = new HashMap<>();
    private final Map< String, String > headers = new HashMap<>();

    private String tokenForGai;
    private String tokenForFio;
    private String tokenForPassport;

    public static SerDes getSerDes () { return serDes != null ? serDes : ( serDes = new SerDes() ); }

    public <T> List<T> stringToArrayList ( String object, Class< T[] > clazz ) { return Arrays.asList( this.gson.fromJson( object, clazz ) ); }

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
        this.getFields().put( "CurrentSystem", "40" );
        this.getFields().put( "Login", "SharafIT_PSP" );
        this.getFields().put( "Password" , "Sh@r@fITP@$P" );
        try { this.setTokenForGai( String.valueOf( Unirest.post( "http://172.250.1.65:7101/Agency/token" )
                .fields( this.getFields() )
                .asJson()
                .getBody()
                .getObject()
                .get( "access_token" ) ) );
            this.setTokenForPassport( this.getTokenForGai() );
            this.setTokenForFio(
                    String.valueOf( Unirest.post( "http://172.250.1.203:9292/Auth/Agency/token" )
                            .header("Content-Type", "application/json")
                            .body("{\r\n    \"Login\": \"SharafIT_PSP\",\r\n    \"Password\": \"Sh@r@fITP@$P\",\r\n    \"CurrentSystem\": \"40\"\r\n}")
                            .asJson()
                            .getBody()
                            .getObject()
                            .get( "access_token" ) ) );
        } catch ( UnirestException e ) { throw new RuntimeException(e); } }

    public Pinpp pinpp ( String pinpp ) { this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForPassport() );
        try { return this.getGson()
                .fromJson( Unirest.get( "http://172.250.1.67:7145/PersonInformation?pinpp=" + pinpp )
                        .headers( this.getHeaders() )
                        .asJson()
                        .getBody()
                        .getObject()
                        .toString(), Pinpp.class ); } catch ( Exception e ) { return new Pinpp(); } }

    public Insurance insurance ( String pinpp ) { this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return this.getGson()
                .fromJson( Unirest.get( "http://172.250.1.67:7145/api/Vehicle/InsuranceInformation?platenumber=" + pinpp )
                        .headers( this.getHeaders() )
                        .asJson()
                        .getBody()
                        .getArray()
                        .get( 0 )
                        .toString(), Insurance.class ); } catch ( Exception e ) { return new Insurance(); } }

    public String getImageByPinfl ( String pinpp ) { this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { JSONObject object = Unirest.get( "http://172.250.1.67:7145/GetPhotoByPinpp?pinpp=" + pinpp )
                .headers( this.getHeaders() )
                .asJson()
                .getBody()
                .getObject();
            return object != null ? object.getString( "Data" ) : "image was not found";
        } catch ( JSONException | UnirestException e ) { return "Error"; } }

    public ModelForCar getVehicleData ( String gosno ) { this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return this.getGson()
                .fromJson( Unirest.get( "http://172.250.1.67:7145/api/Vehicle/VehicleInformation?platenumber=" + gosno )
                        .headers( this.getHeaders() )
                        .asJson()
                        .getBody()
                        .getArray()
                        .get( 0 )
                        .toString(), ModelForCar.class ); } catch ( Exception e ) { return new ModelForCar(); } }

    public Tonirovka getVehicleTonirovka ( String gosno ) { this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return this.getGson().fromJson( Unirest.get( "http://172.250.1.67:7145/api/Vehicle/TintingInformation?platenumber=" + gosno )
                .headers( this.getHeaders() )
                .asJson()
                .getBody()
                .toString(), Tonirovka.class ); } catch ( Exception e ) { return new Tonirovka(); } }

    public ViolationsList getViolationList ( String gosno ) {
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return new ViolationsList( this.stringToArrayList(
                Unirest.get( "http://172.250.1.67:7145/api/Vehicle/ViolationsInformation?PlateNumber=" + gosno )
                        .headers( this.getHeaders() )
                        .asJson()
                        .getBody()
                        .getArray()
                        .toString(), ViolationsInformation[].class ) ); } catch ( Exception e ) { return new ViolationsList( new ArrayList<>() ); } }

    public ModelForCarList getModelForCarList ( String pinfl ) {
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return new ModelForCarList( this.stringToArrayList(
                Unirest.get( "http://172.250.1.67:7145/api/Vehicle/PersonVehiclesInformation?pinpp=" + pinfl )
                        .headers( this.getHeaders() )
                        .asJson()
                        .getBody()
                        .getArray()
                        .toString(), ModelForCar[].class ) ); } catch ( Exception e ) { return new ModelForCarList(); } }

    public DoverennostList getDoverennostList ( String gosno ) {
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return new DoverennostList( this.stringToArrayList(
                Unirest.get( "http://172.250.1.67:7145/api/Vehicle/AttorneyInformation?platenumber=" + gosno )
                        .headers( this.getHeaders() )
                        .asJson()
                        .getBody()
                        .getArray()
                        .toString(), Doverennost[].class ) ); } catch ( Exception e ) { return new DoverennostList( new ArrayList<>() ); } }

    private ModelForAddress getModelForAddress ( String pinfl ) {
        try { this.getFields().clear();
            this.getFields().put( "Pcitizen", pinfl );
            this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
            return this.getGson().fromJson( Unirest.post( "http://172.250.1.67:7121/api/CensusOut/GetAddress" )
                    .headers( this.getHeaders() )
                    .field( "Pcitizen", pinfl )
                    .asJson()
                    .getBody()
                    .getObject()
                    .get( "Data" )
                    .toString(), ModelForAddress.class ); } catch ( Exception e ) { return new ModelForAddress(); } }

    public Mono< PersonTotalDataByFIO > getPersonTotalDataByFIO ( FIO fio ) {
        if ( fio.getSurname() == null
                ^ fio.getName() == null
                && fio.getPatronym() == null ) return Mono.just( new PersonTotalDataByFIO() );
        this.getFields().clear();
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForFio() );
        this.getFields().put( "Surname", fio.getSurname().toUpperCase( Locale.ROOT ) );
        this.getFields().put( "Name", fio.getName() != null ? fio.getName().toUpperCase( Locale.ROOT ) : null );
        this.getFields().put( "Patronym", fio.getPatronym() != null ? fio.getPatronym().toUpperCase( Locale.ROOT ) : null );
        try { PersonTotalDataByFIO person = this.getGson()
                .fromJson( Unirest.post( "http://172.250.1.203:9292/Zags/api/v1/ZagsReference/GetPersonInfo" )
                                .headers( this.getHeaders() )
                                .fields( this.getFields() )
                                .asString()
                                .getBody(),
                        PersonTotalDataByFIO.class );
            if ( person != null && person.getData().size() > 0 ) person.getData()
                    .forEach( person1 -> person1.setPersonImage( this.getImageByPinfl( person1.getPinpp() ) ) );
            return Mono.just( person != null ? person : new PersonTotalDataByFIO() );
        } catch ( Exception e ) { return Mono.just( new PersonTotalDataByFIO() ); } }

    public com.ssd.mvd.entity.modelForCadastr.Data deserialize ( String pinfl ) {
        this.getFields().clear();
        this.getFields().put( "Pcadastre", pinfl );
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForPassport() );
        try { JSONObject object = Unirest.post( "http://172.250.1.67:7121/api/CensusOut/PersonsInCadastre" )
                .headers( this.getHeaders() )
                .fields( this.getFields() )
                .asJson()
                .getBody()
                .getObject();
            return object != null ? this.gson.fromJson( object.get( "Data" ).toString(), Data.class ) : new Data();
        } catch ( JSONException | UnirestException e ) { return new Data(); } }

    public com.ssd.mvd.entity.modelForPassport.Data  deserialize ( String SerialNumber, String BirthDate ) {
        this.getFields().clear();
        this.getFields().put( "BirthDate", BirthDate );
        this.getFields().put( "SerialNumber", SerialNumber );
        System.out.println( "Data: " + SerialNumber + " : " + BirthDate );
        this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForPassport() );
        try { return this.getGson()
                .fromJson( Unirest.post( "http://172.250.1.67:7121/api/CensusOut/GetPerson" )
                        .headers( this.getHeaders() )
                        .fields( this.getFields() )
                        .asJson()
                        .getBody()
                        .getObject()
                        .get( "Data" )
                        .toString(), com.ssd.mvd.entity.modelForPassport.Data.class ); }
        catch ( Exception e ) { return new com.ssd.mvd.entity.modelForPassport.Data(); } }

    private void findAllDataAboutCar ( PsychologyCard psychologyCard ) {
        psychologyCard.getModelForCarList().getModelForCarList().forEach( modelForCar -> {
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
                .getPermanentRegistration().size() > 0 ) {
            psychologyCard.setModelForPassport( this.deserialize(
                    psychologyCard
                            .getModelForCadastr()
                            .getPermanentRegistration()
                            .get( 0 )
                            .getPPsp(),
                    psychologyCard
                            .getModelForCadastr()
                            .getPermanentRegistration()
                            .get( 0 )
                            .getPDateBirth() ) );
            psychologyCard.setModelForAddress( this.getModelForAddress(
                    psychologyCard
                            .getModelForCadastr()
                            .getPermanentRegistration()
                            .get( 0 )
                            .getPCitizen() ) ); } }

    private void setFamilyData ( Results results, PsychologyCard psychologyCard ) {
        psychologyCard.setChildData( results.getChildData() );
        psychologyCard.setMommyData( results.getMommyData() );
        psychologyCard.setDaddyData( results.getDaddyData() );

        if ( psychologyCard.getChildData() != null
                && !psychologyCard.getChildData().getItems().isEmpty()
                && psychologyCard.getChildData().getItems().size() > 0 ) psychologyCard
                .getChildData()
                .getItems()
                .forEach( familyMember -> this.getImageByPinfl( familyMember.getPnfl() ) );

        if ( psychologyCard.getDaddyData() != null
                && !psychologyCard.getDaddyData().getItems().isEmpty()
                && psychologyCard.getDaddyData().getItems().size() > 0 ) psychologyCard
                .getDaddyData()
                .getItems()
                .forEach( familyMember -> this.getImageByPinfl( familyMember.getPnfl() ) );

        if ( psychologyCard.getMommyData() != null
                && !psychologyCard.getMommyData().getItems().isEmpty()
                && psychologyCard.getMommyData().getItems().size() > 0 ) psychologyCard
                .getMommyData()
                .getItems()
                .forEach( familyMember -> this.getImageByPinfl( familyMember.getPnfl() ) ); }

    public PsychologyCard getPsychologyCard ( String pinfl ) {
        if ( pinfl == null ^ pinfl.equals( "null" ) ^ pinfl.length() == 0 ) return null;
        PsychologyCard psychologyCard = new PsychologyCard();
        try { FindFaceComponent
                    .getInstance()
                    .getViolationListByPinfl( pinfl )
                    .doOnError( throwable -> {
                        System.out.println( "ERROR before sending request: " + throwable.getCause() );
                        System.out.println( "ERROR before sending request: " + throwable.getMessage() ); } )
                    .subscribe( list -> psychologyCard.setViolationList( list != null ? list : new ArrayList<>() ) );
        } catch ( Exception e ) { psychologyCard.setViolationList( new ArrayList<>() ); }

        try { FindFaceComponent
                    .getInstance()
                    .getFamilyMembersData( pinfl )
                    .subscribe( results -> this.setFamilyData( results, psychologyCard ) );
        } catch ( Exception e ) {
            psychologyCard.setDaddyData( null );
            psychologyCard.setMommyData( null );
            psychologyCard.setChildData( null ); }

        psychologyCard.setPinpp( this.pinpp( pinfl ) );
        psychologyCard.setPersonImage( this.getImageByPinfl( pinfl ) );
        psychologyCard.setModelForCarList( this.getModelForCarList( pinfl ) );
        if ( psychologyCard.getModelForCarList() != null
                && psychologyCard
                .getModelForCarList()
                .getModelForCarList() != null
                && psychologyCard
                .getModelForCarList()
                .getModelForCarList()
                .size() > 0 ) this.findAllDataAboutCar( psychologyCard );
        this.setPersonPrivateData( psychologyCard );
        return psychologyCard; }

    public PsychologyCard getPsychologyCard ( String passport, Results results ) { // returns the card in case of Person
        PsychologyCard psychologyCard = new PsychologyCard();
        try { if ( passport.length() == 9 ) passport = passport.replace( "-", "0" );
        else passport = passport.replace( "-", "" );
            this.setFamilyData( results, psychologyCard );
            psychologyCard.setPapilonData( results.getResults() );
            psychologyCard.setViolationList( results.getViolationList() );
            psychologyCard.setPinpp( this.pinpp(
                    results
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
            if ( psychologyCard.getModelForCarList() != null
                    && psychologyCard
                    .getModelForCarList()
                    .getModelForCarList()
                    .size() > 0 ) this.findAllDataAboutCar( psychologyCard );
            this.setPersonPrivateData( psychologyCard );
            return psychologyCard;
        } catch ( Exception e ) { return psychologyCard; } }

    public PsychologyCard getPsychologyCard ( com.ssd.mvd.entity.modelForPassport.Data data ) {
        PsychologyCard psychologyCard = new PsychologyCard();
        if ( data.getPerson() == null ) return psychologyCard;
        psychologyCard.setModelForPassport( data );
        try {
            FindFaceComponent
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
        if ( psychologyCard.getModelForCarList() != null
                && psychologyCard
                .getModelForCarList()
                .getModelForCarList()
                .size() > 0 ) this.findAllDataAboutCar( psychologyCard );
        psychologyCard.setModelForCadastr( this.deserialize( psychologyCard.getPinpp().getCadastre() ) );
        return psychologyCard; }

    @Override
    public void run () {
        while ( true ) {
            this.updateTokens();
            System.out.println( "Updating tokens..." );
            try { Thread.sleep( 60 * 60 * 1000 ); } catch ( InterruptedException e ) { e.printStackTrace(); } } }
}

