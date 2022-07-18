//package com.ssd.mvd.database;
//
//import com.ssd.mvd.entity.ApiResponseModel;
//import com.ssd.mvd.constants.Status;
//import com.ssd.mvd.entity.Patrul;
//
//import org.redisson.api.*;
//import org.redisson.Redisson;
//import org.redisson.config.Config;
//import reactor.core.publisher.Mono;
//
//public final class RedisDataControl {
//    private final RMapReactive< String, String > patrulMap;
//    private final RedissonReactiveClient redissonReactiveClient;
//
//    private static RedisDataControl redisDataControl = new RedisDataControl();
//
//    public static RedisDataControl getRedis () { return redisDataControl != null ? redisDataControl : ( redisDataControl = new RedisDataControl() ); }
//
//    private RedisDataControl () {
//        Config config = new Config();
//        config.useSingleServer().setAddress( "redis://10.254.1.227:6367" ).setClientName( "default" ).setPassword( "8tRk62" );
//        this.redissonReactiveClient = Redisson.createReactive( config );
//        this.patrulMap = this.redissonReactiveClient.getMap( "patrulMap" ); } // for cars
//
//    public Mono< Patrul > getPatrul ( String passportNumber ) { return this.patrulMap.containsKey( passportNumber ).flatMap(value -> value ? this.patrulMap.get( passportNumber ).map( s -> Serdes.getInstance().deserializePatrul( s ) ) : Mono.empty() ); }
//
//    public Mono< ApiResponseModel > update ( String passportNumber, String findFaceTask ) { return this.patrulMap.containsKey( passportNumber ).flatMap( aBoolean -> aBoolean ?
//            this.getPatrul( passportNumber ).flatMap( patrul -> {
//                patrul.changeTaskStatus( Status.ATTACHED ).setFindFaceTask( findFaceTask );
//                return this.patrulMap.fastPutIfExists( patrul.getPassportNumber(), Serdes.getInstance().serializePatrul( patrul ) ).flatMap( aBoolean1 -> Mono.just( ApiResponseModel.builder().success( CassandraDataControl.getInstance().addValue( patrul, Serdes.getInstance().serializePatrul( patrul ) ) ).status( com.ssd.mvd.entity.Status.builder().code( 200 ).message( "Patrul: " + patrul.getName() + " linked to card" ).build() ).build() ) );
//            } ).log().doOnError( throwable -> this.clear() ) : Mono.just( ApiResponseModel.builder().success( false ).status( com.ssd.mvd.entity.Status.builder().message( "Wrong Patrul data" ).code( 201 ).build() ).build() ) ); }
//
//    public Mono< ApiResponseModel > update ( String passportNumber ) { return this.patrulMap.containsKey( passportNumber ).flatMap( aBoolean -> aBoolean ?
//            this.getPatrul( passportNumber ).flatMap( patrul -> this.patrulMap.fastPutIfExists( patrul.getPassportNumber(), Serdes.getInstance().serializePatrul( patrul ) ).flatMap( aBoolean1 -> Mono.just( ApiResponseModel.builder().success( CassandraDataControl.getInstance().addValue( patrul, Serdes.getInstance().serializePatrul( patrul ) ) ).status( com.ssd.mvd.entity.Status.builder().code( 200 ).message( "Patrul: " + patrul.getName() + " linked to card" ).build() ).build() ) ) )
//                    .log().doOnError( throwable -> this.clear() ) : Mono.just( ApiResponseModel.builder().success( false ).status( com.ssd.mvd.entity.Status.builder().message( "Wrong Patrul data" ).code( 201 ).build() ).build() ) ); }
//
//    public void clear () {
//        this.patrulMap.delete().onErrorStop().log().subscribe();
//        this.redissonReactiveClient.shutdown();
//        redisDataControl = null; }
//}
