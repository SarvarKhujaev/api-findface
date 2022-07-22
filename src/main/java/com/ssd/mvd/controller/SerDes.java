package com.ssd.mvd.controller;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.ssd.mvd.entity.*;
import com.ssd.mvd.entity.modelForGai.*;
import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.entity.modelForCadastr.Data;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

@lombok.Data
public class SerDes {
    private final Gson gson = new Gson();
    private static SerDes serDes = new SerDes();
    private final Map< String, Object > fields = new HashMap<>();
    private final Map< String, String > headers = new HashMap<>();
    private String tokenForPassport = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoiIiwiVXNlcklkIjoiMTAwMTAxMCIsIlN1YnN5c3RlbSI6IjEiLCJMT0NBTCBBVVRIT1JJVFkiOiJBc2J0QXV0aDIuMFNlcnZlciIsImh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vd3MvMjAwOC8wNi9pZGVudGl0eS9jbGFpbXMvcm9sZSI6WyIxMDEyMDAwIiwiMTAxMjAwMSIsIjEwMTIwMDIiLCIxMDEyMDAzIiwiMTAxMjAwNCJdLCJuYmYiOjE2NTcwMjQyNjcsImV4cCI6MTY1NzExMDY2NywiaXNzIjoiQXNidEF1dGgyLjBTZXJ2ZXIiLCJhdWQiOiJodHRwOi8vYXNidC51ei8ifQ.OF9-vsxindRQgR_i9kBquFGePh8k6M7-5w2UskjQCd8";
    private String tokenForGai = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoiIiwiVXNlcklkIjoiMTAwMTAxMCIsIlN1YnN5c3RlbSI6IjQwIiwiTE9DQUwgQVVUSE9SSVRZIjoiQXNidEF1dGgyLjBTZXJ2ZXIiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3JvbGUiOlsiMTAxMjAwMCIsIjEwMTIwMDEiLCIxMDEyMDAyIiwiMTAxMjAwMyIsIjEwMTIwMDQiXSwibmJmIjoxNjU2NTgyOTkyLCJleHAiOjE2NTY2NjkzOTIsImlzcyI6IkFzYnRBdXRoMi4wU2VydmVyIiwiYXVkIjoiaHR0cDovL2FzYnQudXovIn0.tyeEiazjrHMths2caBs4BvJmE5GLLxTnRa8-rKa1fHY";

    public static SerDes getSerDes () { return serDes != null ? serDes : ( serDes = new SerDes() ); }

    public <T> List<T> stringToArrayList ( String object, Class< T[] > clazz ) { return Arrays.asList( this.gson.fromJson( object, clazz ) ); }

    private SerDes () {
        Unirest.setObjectMapper( new ObjectMapper() {
            private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

            @Override
            public String writeValue( Object o ) { try { return this.objectMapper.writeValueAsString( o ); } catch (JsonProcessingException e) { throw new RuntimeException(e); } }

            @Override
            public <T> T readValue( String s, Class<T> aClass ) { try { return this.objectMapper.readValue( s, aClass ); } catch (JsonProcessingException e) { throw new RuntimeException(e); } } } );
        this.getHeaders().put("accept", "application/json");
        this.updateTokens(); }

    public void updateTokens () {
        this.getFields().put( "CurrentSystem" , "40" );
        this.getFields().put( "Login", "SharafIT_PSP" );
        this.getFields().put( "Password" , "Sh@r@fITP@$P" );
        try { this.setTokenForGai( String.valueOf( Unirest.post( "http://172.250.1.65:7101/Agency/token" ).fields( this.getFields() ).asJson().getBody().getObject().get( "access_token" ) ) );
            this.setTokenForPassport( this.getTokenForGai() );
        } catch ( UnirestException e ) { throw new RuntimeException(e); } }

    public com.ssd.mvd.entity.modelForCadastr.Data deserialize ( String pinfl ) {
        this.getFields().clear();
        this.getFields().put( "Pcadastre", pinfl );
        this.headers.put( "Authorization", "Bearer " + this.getTokenForPassport() );
        try { JSONObject object = Unirest.post( "http://172.250.1.67:7121/api/CensusOut/PersonsInCadastre" )
                .headers( this.getHeaders() )
                .fields( this.getFields() ).asJson().getBody().getObject();
            return object != null ? this.gson.fromJson( object.get( "Data" ).toString(), Data.class ) : new Data();
        } catch ( JSONException | UnirestException e ) { return new Data(); } }

    public PsychologyCard getPsychologyCard ( String passport, List< PapilonData > results, List< Violation > violationList ) { // returns the card in case of Person
        PsychologyCard psychologyCard = new PsychologyCard();
        if ( passport.length() == 9 ) passport = passport.replace( "-", "0" );
        else passport = passport.replace( "-", "" );
        psychologyCard.setPapilonData( results );
        psychologyCard.setViolationList( violationList );
        psychologyCard.setPinpp( SerDes.getSerDes().pinpp( results.get( 0 ).getPersonal_code() ) );
        psychologyCard.setPersonImage( this.getImageByPnfl( results.get( 0 ).getPersonal_code() ) );
        psychologyCard.setModelForCadastr( SerDes.getSerDes().deserialize( psychologyCard.getPinpp().getCadastre() ) );
        psychologyCard.setModelForCarList( SerDes.getSerDes().getModelForCarList( results.get( 0 ).getPersonal_code() ) );
        String[] dates = psychologyCard.getPinpp().getBirthDate().split( "-" );
        System.out.println( psychologyCard.getPinpp().getBirthDate() );
        if ( dates.length == 3 ) {
            String data = dates[2] + "." + dates[1] + "." + dates[0];
            System.out.println( passport );
            System.out.println( data );
            psychologyCard.setModelForPassport( SerDes.getSerDes().deserialize( passport, data ) ); }
        return psychologyCard; }

    public com.ssd.mvd.entity.modelForPassport.Data  deserialize ( String SerialNumber, String BirthDate ) {
        this.getFields().clear();
        this.getFields().put( "BirthDate", BirthDate );
        this.getFields().put( "SerialNumber", SerialNumber );
        this.headers.put("Authorization", "Bearer " + this.getTokenForPassport() );
        try { return this.getGson().fromJson( Unirest.post( "http://172.250.1.67:7121/api/CensusOut/GetPerson" )
                .headers( this.getHeaders() )
                .fields( this.getFields() )
                .asJson()
                .getBody()
                .getObject()
                .get( "Data" )
                .toString(), com.ssd.mvd.entity.modelForPassport.Data.class ); }
        catch ( Exception e ) { return new com.ssd.mvd.entity.modelForPassport.Data(); } }

    public Pinpp pinpp ( String pinpp ) { this.headers.put( "Authorization", "Bearer " + this.getTokenForPassport() );
        try { return this.getGson().fromJson( Unirest.get( "http://172.250.1.67:7145/PersonInformation?pinpp=" + pinpp )
                .headers( this.getHeaders() )
                .asJson()
                .getBody()
                .getObject()
                .toString(), Pinpp.class ); } catch ( Exception e ) { return new Pinpp(); } }

    public Insurance insurance ( String pinpp ) { this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return this.getGson().fromJson( Unirest.get( "http://172.250.1.67:7145/api/Vehicle/InsuranceInformation?platenumber=" + pinpp ).headers( this.getHeaders() ).asJson().getBody().getArray().get(0).toString(), Insurance.class ); } catch ( Exception e ) { return new Insurance(); } }

    public String getImageByPnfl ( String pinpp ) { this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try {
            JSONObject object = Unirest.get( "http://172.250.1.67:7145/GetPhotoByPinpp?pinpp=" + pinpp ).headers( this.getHeaders() ).asJson().getBody().getObject();
            return object != null ? object.getString( "Data" ) : "image was not found";
        } catch ( JSONException | UnirestException e ) { return "Error"; } }

    public Tonirovka getVehicleTonirovka ( String gosno ) { this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return this.getGson().fromJson( Unirest.get( "http://172.250.1.67:7145/api/Vehicle/TintingInformation?platenumber=" + gosno ).headers( this.getHeaders() ).asJson().getBody().toString(), Tonirovka.class ); } catch ( Exception e ) { return new Tonirovka(); } }

    public ModelForCar getVehicleData ( String gosno ) { this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return this.getGson().fromJson( Unirest.get( "http://172.250.1.67:7145/api/Vehicle/VehicleInformation?platenumber=" + gosno ).headers( this.getHeaders() ).asJson().getBody().getArray().get(0).toString(), ModelForCar.class ); } catch ( Exception e ) { return new ModelForCar(); } }

    public ModelForCarList getModelForCarList ( String pinfl ) { this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return new ModelForCarList( this.stringToArrayList( Unirest.get( "http://172.250.1.67:7145/api/Vehicle/PersonVehiclesInformation?pinpp=" + pinfl ).headers( this.getHeaders() ).asJson().getBody().getArray().toString(), ModelForCar[].class ) ); } catch ( Exception e ) { return new ModelForCarList(); } }

    public DoverennostList getDoverennostList ( String gosno ) { this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return new DoverennostList( this.stringToArrayList( Unirest.get( "http://172.250.1.67:7145/api/Vehicle/AttorneyInformation?platenumber=" + gosno ).headers( this.getHeaders() ).asJson().getBody().getArray().toString(), Doverennost[].class ) ); } catch ( Exception e ) { return new DoverennostList( new ArrayList<>() ); } }

    public ViolationsList getViolationList ( String gosno ) { this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return new ViolationsList( this.stringToArrayList( Unirest.get( "http://172.250.1.67:7145/api/Vehicle/ViolationsInformation?PlateNumber=" + gosno ).headers( this.getHeaders() ).asJson().getBody().getArray().toString(), ViolationsInformation[].class ) ); } catch ( Exception e ) { return new ViolationsList( new ArrayList<>() ); } }

    public PsychologyCard getPsychologyCard( String pinfl ) {
        if ( pinfl == null ) { return null; }
        PsychologyCard psychologyCard = new PsychologyCard();
        FindFaceComponent.getInstance().getViolationListByPinfl( pinfl ).subscribe( value -> {
            if ( value != null ) psychologyCard.setViolationList( value );
            else psychologyCard.setViolationList( new ArrayList<>() ); } );
        psychologyCard.setPinpp( SerDes.getSerDes().pinpp( pinfl ) );
        psychologyCard.setPersonImage( this.getImageByPnfl( pinfl ) );
        psychologyCard.setModelForCarList( SerDes.getSerDes().getModelForCarList( pinfl ) );
        psychologyCard.setModelForCadastr( SerDes.getSerDes().deserialize( psychologyCard.getPinpp().getCadastre() ) );
        if ( psychologyCard.getModelForCadastr() != null && psychologyCard.getModelForCadastr().getPermanentRegistration().size() > 0 )
            psychologyCard.setModelForPassport( this.deserialize( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPPsp(),
                    psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPDateBirth() ) );
        return psychologyCard; }

    public PsychologyCard getPsychologyCard( com.ssd.mvd.entity.modelForPassport.Data data ) {
        PsychologyCard psychologyCard = new PsychologyCard();
        if ( data.getPerson() == null ) return psychologyCard;
        psychologyCard.setModelForPassport( data );
        FindFaceComponent.getInstance().getViolationListByPinfl( data.getPerson().getPinpp() ).subscribe( value -> {
            if ( value != null ) psychologyCard.setViolationList( value );
            else psychologyCard.setViolationList( new ArrayList<>() ); } );
        psychologyCard.setPinpp( SerDes.getSerDes().pinpp( data.getPerson().getPinpp() ) );
        psychologyCard.setPersonImage( this.getImageByPnfl( data.getPerson().getPinpp() ) );
        psychologyCard.setModelForCarList( SerDes.getSerDes().getModelForCarList( data.getPerson().getPinpp() ) );
        psychologyCard.setModelForCadastr( SerDes.getSerDes().deserialize( psychologyCard.getPinpp().getCadastre() ) );
        return psychologyCard; }
}
