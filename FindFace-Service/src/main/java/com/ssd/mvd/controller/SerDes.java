package com.ssd.mvd.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.ssd.mvd.entity.Pinpp;
import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.modelForCadastr.ModelForCadastor;
import com.ssd.mvd.entity.modelForGai.*;
import com.ssd.mvd.entity.modelForPassport.ModelForPassport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@lombok.Data
public class SerDes {
    private final Gson gson = new Gson();
    private static SerDes serDes = new SerDes();
    private final Map< String, Object > fields = new HashMap<>();
    private final Map< String, String > headers = new HashMap<>();
    private String tokenForPassport = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoiIiwiVXNlcklkIjoiMTAwMTAxMCIsIlN1YnN5c3RlbSI6IjEiLCJMT0NBTCBBVVRIT1JJVFkiOiJBc2J0QXV0aDIuMFNlcnZlciIsImh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vd3MvMjAwOC8wNi9pZGVudGl0eS9jbGFpbXMvcm9sZSI6IjEwMTIwMDAiLCJuYmYiOjE2NTQyNjg3MjAsImV4cCI6MTY1NTEzMjcyMCwiaXNzIjoiQXNidEF1dGgyLjBTZXJ2ZXIiLCJhdWQiOiJodHRwOi8vYXNidC51ei8ifQ.5HzXqMANiy5znL6IkKrzgNJku7WFxjyQFYvBMgsbajE";
    private String tokenForGai = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoiIiwiVXNlcklkIjoiMTAwMTAxMCIsIlN1YnN5c3RlbSI6IjQwIiwiTE9DQUwgQVVUSE9SSVRZIjoiQXNidEF1dGgyLjBTZXJ2ZXIiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3JvbGUiOlsiMTAxMjAwMCIsIjEwMTIwMDEiLCIxMDEyMDAyIiwiMTAxMjAwMyIsIjEwMTIwMDQiXSwibmJmIjoxNjU0MzM2MzIwLCJleHAiOjE2NTQ0MjI3MjAsImlzcyI6IkFzYnRBdXRoMi4wU2VydmVyIiwiYXVkIjoiaHR0cDovL2FzYnQudXovIn0.MdKSogowgmJxHeh2foklKSHIAGQbgK2q5C-C0vYdjZY";

    public static SerDes getSerDes () { return serDes != null ? serDes : ( serDes = new SerDes() ); }

    private SerDes () {
        Unirest.setObjectMapper( new ObjectMapper() {
            private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

            @Override
            public String writeValue(Object o) { try { return this.objectMapper.writeValueAsString( o ); } catch (JsonProcessingException e) { throw new RuntimeException(e); } }

            @Override
            public <T> T readValue( String s, Class<T> aClass ) { try { return this.objectMapper.readValue( s, aClass ); } catch (JsonProcessingException e) { throw new RuntimeException(e); } } } );
        this.getHeaders().put("accept", "application/json"); }

    public ModelForPassport deserialize ( String SerialNumber, String BirthDate ) {
        this.getFields().clear();
        this.getFields().put( "BirthDate", BirthDate );
        this.getFields().put( "SerialNumber", SerialNumber );
        this.headers.put("Authorization", "Bearer " + this.getTokenForPassport() );
        try { return this.getGson().fromJson( Unirest.post( "http://172.250.1.67:7121/api/CensusOut/GetPerson" ).headers( this.getHeaders() ).fields( this.getFields() ).asJson().getBody().toString(), ModelForPassport.class ); } catch ( UnirestException e ) { throw new RuntimeException(e); } }

    public ModelForAddress deserialize ( String Pcitizen, Boolean value ) {
        this.getFields().clear();
        this.getFields().put( "Pcitizen", Pcitizen );
        this.headers.put("Authorization", "Bearer " + this.getTokenForPassport() );
        if ( value ) try { return this.getGson().fromJson( Unirest.post( "http://172.250.1.67:7121/api/CensusOut/GetAddress" ).headers( this.getHeaders() ).fields( this.getFields() ).asJson().getBody().toString(), ModelForAddress.class ); } catch ( UnirestException e ) { throw new RuntimeException(e); }
        else return null; }

    public ModelForCadastor deserialize ( String Pcadastre ) {
        this.getFields().clear();
        this.getFields().put( "Pcadastre", Pcadastre );
        this.headers.put("Authorization", "Bearer " + this.getTokenForPassport() );
        try { return this.getGson().fromJson( Unirest.post( "http://172.250.1.67:7121/api/CensusOut/PersonsInCadastre" ).headers( this.getHeaders() ).fields( this.getFields() ).asJson().getBody().toString(), ModelForCadastor.class ); } catch ( UnirestException e ) { throw new RuntimeException(e); } }

    public Pinpp pinpp ( String pinpp ) { this.headers.put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return this.getGson().fromJson( Unirest.get( "http://172.250.1.67:7145/PersonInformation?pinpp=" + pinpp ).headers( this.getHeaders() ).asJson().getBody().getObject().toString(), Pinpp.class ); } catch ( UnirestException e ) { throw new RuntimeException(e); } }

    public String getPhotoByPinpp ( String pinpp ) { this.headers.put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return Unirest.get( "http://172.250.1.67:7145/GetPhotoByPinpp?pinpp=" + pinpp ).headers( this.getHeaders() ).asJson().getBody().getObject().getString( "Data" ); } catch ( UnirestException e ) { throw new RuntimeException(e); } }

    public ModelForGai modelForGai ( String pinpp ) { this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return this.getGson().fromJson( Unirest.get( "http://172.250.1.67:7145/api/Vehicle/PersonVehiclesInformation?pinpp=" + pinpp ).headers( this.getHeaders() ).asJson().getBody().getArray().get(0).toString(), ModelForGai.class ); } catch ( UnirestException e ) { throw new RuntimeException(e); } }

    public ModelForCar getVehicleData ( String gosno ) { this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return this.getGson().fromJson( Unirest.get( "http://172.250.1.67:7145/api/Vehicle/VehicleInformation?platenumber=" + gosno ).headers( this.getHeaders() ).asJson().getBody().getArray().get(0).toString(), ModelForCar.class ); } catch ( UnirestException e ) { throw new RuntimeException(e); } }

    public Tonirovka getVehicleTonirovka ( String gosno ) { this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return this.getGson().fromJson( Unirest.get( "http://172.250.1.67:7145/api/Vehicle/TintingInformation?platenumber=" + gosno ).headers( this.getHeaders() ).asJson().getBody().toString(), Tonirovka.class ); } catch ( UnirestException e ) { throw new RuntimeException(e); } }

    public ViolationsList getViolationList ( String gosno ) { this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return new ViolationsList( this.getGson().fromJson( Unirest.get( "http://172.250.1.67:7145/api/Vehicle/ViolationsInformation?PlateNumber=" + gosno ).headers( this.getHeaders() ).asJson().getBody().getArray().toString(), List.class ) ); } catch (UnirestException e ) { throw new RuntimeException(e); } }

    public DoverennostList getDoverennostList ( String gosno ) { this.getHeaders().put( "Authorization", "Bearer " + this.getTokenForGai() );
        try { return new DoverennostList( this.getGson().fromJson( Unirest.get( "http://172.250.1.67:7145/api/Vehicle/AttorneyInformation?platenumber=" + gosno ).headers( this.getHeaders() ).asJson().getBody().getArray().toString(), List.class ) ); } catch (UnirestException e ) { throw new RuntimeException(e); } }
}
