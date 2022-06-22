package com.ssd.mvd.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.ssd.mvd.entity.CarTotalData;
import lombok.Data;

@Data
public class Serdes {
    private final Gson gson = new Gson();
    private static Serdes serdes = new Serdes();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> String serialize(T object) { return this.getGson().toJson( object ); }

    public static Serdes getInstance() { return serdes != null ? serdes : ( serdes = new Serdes() ); }

    public CarTotalData deserialize ( String item ) { return this.getGson().fromJson( item, CarTotalData.class ); }
}
