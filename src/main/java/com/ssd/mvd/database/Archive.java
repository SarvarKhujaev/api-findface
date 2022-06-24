package com.ssd.mvd.database;

import com.ssd.mvd.controller.SerDes;
import com.ssd.mvd.entity.CarTotalData;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.entity.modelForFindFace.PreferenceItem;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
}
