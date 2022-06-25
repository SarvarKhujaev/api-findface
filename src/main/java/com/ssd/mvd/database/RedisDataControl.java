package com.ssd.mvd.database;

import com.ssd.mvd.entity.Patrul;
import org.redisson.api.*;
import org.redisson.Redisson;
import org.redisson.config.Config;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class RedisDataControl {
    private final RMapReactive< String, String > patrulMap;
    private final RedissonReactiveClient redissonReactiveClient;

    private static RedisDataControl redisDataControl = new RedisDataControl();

    public static RedisDataControl getRedis () { return redisDataControl != null ? redisDataControl : ( redisDataControl = new RedisDataControl() ); }

    private RedisDataControl () {
        Config config = new Config();
        config.useSingleServer().setAddress( "redis://10.254.1.227:6367" ).setClientName( "default" ).setPassword( "8tRk62" );
        this.redissonReactiveClient = Redisson.createReactive( config );
        this.patrulMap = this.redissonReactiveClient.getMap( "patrulMap" ); } // for cars

    public Flux< Patrul > getAllPatruls () { return this.patrulMap.valueIterator().map(value -> Serdes.getInstance().deserializePatrul( value ) ); }

    public Mono< Patrul > getPatrul (String passportNumber ) { return this.patrulMap.containsKey( passportNumber ).flatMap(value -> value ? this.patrulMap.get( passportNumber ).map( s -> Serdes.getInstance().deserializePatrul( s ) ) : Mono.empty() ); }

    public void clear () {
        this.patrulMap.delete().onErrorStop().log().subscribe();
        this.redissonReactiveClient.shutdown();
        redisDataControl = null; }
}
