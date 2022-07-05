package com.ssd.mvd.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssd.mvd.entity.CarTotalData;
import com.ssd.mvd.entity.Patrul;
import com.google.gson.Gson;
import lombok.Data;

@Data
public class Serdes {
    private final Gson gson = new Gson();
    private static Serdes serdes = new Serdes();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> String serialize( T object ) { return this.getGson().toJson( object ); }

    public String serializePatrul ( Patrul patrul ) { return this.getGson().toJson( patrul ); }

    public static Serdes getInstance() { return serdes != null ? serdes : ( serdes = new Serdes() ); }

    public Patrul deserializePatrul ( String item ) { return this.getGson().fromJson( item,   Patrul.class ); }

    public CarTotalData deserialize ( String item ) { return this.getGson().fromJson( item, CarTotalData.class ); }
}
