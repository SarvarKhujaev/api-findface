//package com.ssd.mvd.database;
//
//import com.ssd.mvd.controller.SerDes;
//import com.ssd.mvd.constants.Status;
//import com.ssd.mvd.entity.*;
//
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import lombok.extern.slf4j.Slf4j;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import lombok.Data;
//
//@Data
//@Slf4j
//public class Archive implements Runnable {
//    private Boolean flag = true;
//    private static Archive archive = new Archive();
//    private final Map< String, CarTotalData > preferenceItemMapForCar = new HashMap<>();
//    private final Map< String, PsychologyCard > preferenceItemMapForFace = new HashMap<>();
//
//    public static Archive getInstance() { return archive != null ? archive : ( archive = new Archive() ); }
//
//    public Archive () { SerDes.getSerDes(); }
//
//    public CarTotalData getCarTotalData ( String id ) { return this.getPreferenceItemMapForCar().get( id ); }
//
//    public CarTotalData save ( CarTotalData carTotalData ) {
//        carTotalData.setModelForCarList( SerDes.getSerDes().getModelForCarList( carTotalData.getModelForCar().getPinpp() ) );
//        this.getPreferenceItemMapForCar().putIfAbsent( carTotalData.getGosNumber(), carTotalData );
//        return carTotalData; }
//
//    // links Patrul to existing Card
//    public Mono< ApiResponseModel > save ( Request request ) {
//        if ( this.getPreferenceItemMapForCar().get( request.getAdditional() ).getPatruls() == null ) this.getPreferenceItemMapForCar().get( request.getAdditional() ).setPatruls( new ArrayList<>() );
//        this.getPreferenceItemMapForCar().get( request.getAdditional() ).getPatruls().add( request.getData() );
//        return RedisDataControl.getRedis().update( request.getData(), request.getAdditional() ); }
//
//    public Mono< ApiResponseModel > save ( ReportForCard reportForCard ) { return RedisDataControl.getRedis().getPatrul( reportForCard.getPassportSeries() ).flatMap( patrul -> {
//        this.getPreferenceItemMapForCar().get( patrul.getFindFaceTask() ).getReportForCards().add( reportForCard );
//        return RedisDataControl.getRedis().update( patrul.getPassportNumber() ).flatMap( apiResponseModel -> Mono.just( ApiResponseModel.builder().success( CassandraDataControl.getInstance().addValue( patrul, Serdes.getInstance().serializePatrul( patrul ) ) )
//                .status( com.ssd.mvd.entity.Status.builder().message( "Report from: " + patrul.getName() + " was saved" ).code( 200 ).build() ).build() ) ); } ); }
//
//    @Override
//    public void run() {
//        while ( this.getFlag() ) { try { Thread.sleep( 300 * 1000 ); } catch (InterruptedException e) { e.printStackTrace(); }
//            SerDes.getSerDes().updateTokens();
//            Flux.fromStream( this.getPreferenceItemMapForCar().values().stream() ).filter( carTotalData -> carTotalData.getStatus().compareTo( Status.FINISHED ) == 0 ).subscribe( carTotalData -> {
//                        this.getPreferenceItemMapForCar().remove( carTotalData );
//                        CassandraDataControl.getInstance().addValue( carTotalData ); } ); } }
//}
