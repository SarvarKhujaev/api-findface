package com.ssd.mvd.database;

import com.ssd.mvd.controller.SerDes;
import com.ssd.mvd.entity.CarTotalData;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.entity.modelForFindFace.PreferenceItem;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@Slf4j
public class Archive {
    private static Archive archive = new Archive();
    private final Map< String, CarTotalData > preferenceItemMapForCar = new HashMap<>();
    private final Map< String, PsychologyCard > preferenceItemMapForFace = new HashMap<>();

    public static Archive getInstance() { return archive != null ? archive : ( archive = new Archive() ); }

    public void save ( PreferenceItem preferenceItem ) {
        if ( preferenceItem.getWebhook_type().equals( "car_event" ) ) this.getPreferenceItemMapForCar().put( preferenceItem.getFullframe(), null );
        else { Mono.just ( Objects.requireNonNull( this.getPreferenceItemMapForCar().put( preferenceItem.getDetector_params().getTrack().getId(),
                KafkaDataControl.getInstance().writeToKafka( CarTotalData.builder().cameraImage( preferenceItem.getFullframe() )
                        .doverennostList( SerDes.getSerDes().getDoverennostList( preferenceItem.getFeatures().getLicense_plate_number().getName() ) )
                        .violationsList( SerDes.getSerDes().getViolationList( preferenceItem.getFeatures().getLicense_plate_number().getName() ) )
                        .tonirovka( SerDes.getSerDes().getVehicleTonirovka( preferenceItem.getFeatures().getLicense_plate_number().getName() ) )
                        .modelForCar( SerDes.getSerDes().getVehicleData( preferenceItem.getFeatures().getLicense_plate_number().getName() ) )
                        .gosNumber( preferenceItem.getFeatures().getLicense_plate_number().getName() )
                        .confidence( preferenceItem.getFeatures().getModel().getConfidence() )
                        .id ( preferenceItem.getDetector_params().getTrack().getId() )
                        .brand( preferenceItem.getFeatures().getModel().getName() )
                        .color( preferenceItem.getFeatures().getColor().getName() )
                        .type( preferenceItem.getFeatures().getMake().getName() )
                        .matched( preferenceItem.getMatched() ).build() ) ) ) ).onErrorStop().log().subscribe(); } }

    public Mono< PsychologyCard > save ( String pinpp ) { this.getPreferenceItemMapForFace().put( pinpp, KafkaDataControl.getInstance().writeToKafka( PsychologyCard.builder().pinpp( pinpp )
            .modelForPassport( SerDes.getSerDes().deserialize( pinpp, "25/12/12" ) )
            .modelForCadastor( SerDes.getSerDes().deserialize( pinpp ) )
            .modelForGai( SerDes.getSerDes().modelForGai( pinpp ) )
            .build() ) );
        return Mono.just( this.getPreferenceItemMapForFace().get( pinpp ) ).log().doOnError( throwable -> KafkaDataControl.getInstance().clear() ); }
}
