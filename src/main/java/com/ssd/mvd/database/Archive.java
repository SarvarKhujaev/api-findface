package com.ssd.mvd.database;

import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.entity.CarTotalData;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
@Slf4j
public class Archive {
    private static Archive archive = new Archive();
    private final Map< String, CarTotalData > preferenceItemMapForCar = new HashMap<>();
    private final Map< String, PsychologyCard > preferenceItemMapForFace = new HashMap<>();

    public static Archive getInstance() { return archive != null ? archive : ( archive = new Archive() ); }

    public CarTotalData getCarTotalData ( String id ) { return this.getPreferenceItemMapForCar().get( id ); }

    public CarTotalData save ( CarTotalData carTotalData ) {
        this.getPreferenceItemMapForCar().putIfAbsent( carTotalData.getGosNumber(), carTotalData );
        return carTotalData; }
}
