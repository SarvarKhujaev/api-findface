package com.ssd.mvd.database;

import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.ssd.mvd.entity.CarTotalData;
import com.datastax.driver.core.*;
import reactor.core.publisher.Flux;

import java.util.logging.Logger;

public final class CassandraDataControl {
    private final Cluster cluster;
    private final Session session;
    private final String dbName = "FindFace";
    private final String carTotalData = "CarTotalData";
    private final String personTotalData = "PersonTotalData";
    private static CassandraDataControl cassandraDataControl = new CassandraDataControl();
    private final Logger logger = Logger.getLogger( CassandraDataControl.class.toString() );

    public static CassandraDataControl getInstance() { return cassandraDataControl != null ? cassandraDataControl : ( cassandraDataControl = new CassandraDataControl() ); }

    private CassandraDataControl () { ( this.session = ( this.cluster = Cluster.builder().withPort( 9942 ).addContactPoint( "10.254.1.227" ).withProtocolVersion( ProtocolVersion.V4 ).withRetryPolicy( DefaultRetryPolicy.INSTANCE )
                .withSocketOptions( new SocketOptions().setReadTimeoutMillis( 30000 ) ).withLoadBalancingPolicy( new TokenAwarePolicy( DCAwareRoundRobinPolicy.builder().build() ) )
                .withPoolingOptions( new PoolingOptions().setMaxConnectionsPerHost( HostDistance.LOCAL, 1024 ).setMaxRequestsPerConnection( HostDistance.REMOTE, 256 ).setPoolTimeoutMillis( 60000 ) ).build() ).connect() )
                .execute( "CREATE KEYSPACE IF NOT EXISTS " + this.dbName + " WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor':1 };" );
        this.session.execute("CREATE TABLE IF NOT EXISTS " + this.dbName + "." + this.carTotalData + "(id text PRIMARY KEY, object text);" ); // the table for patruls
        this.logger.info( "Cassandra is ready" ); }

    public CarTotalData addValue ( CarTotalData carTotalData ) {
        this.session.executeAsync( "INSERT INTO " + this.dbName + "." + this.carTotalData + "(id, object)" + " VALUES( '" + carTotalData.getGosNumber() + "', '" + Serdes.getInstance().serialize( carTotalData ) + "');" );
        return carTotalData; }

    public void resetData () { Flux.fromStream( this.session.execute( "SELECT * FROM " + this.dbName + "." + this.carTotalData + ";" ).all().stream() ).map( row -> Serdes.getInstance().deserialize( row.getString( "object" ) ) ).subscribe( carTotalData1 -> Archive.getInstance().save( carTotalData1 ) ); }

    public void delete () {
        this.session.close();
        this.cluster.close();
        cassandraDataControl = null;
        this.logger.info( "Cassandra is closed!!!" ); }
}
