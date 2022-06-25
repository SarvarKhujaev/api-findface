package com.ssd.mvd.database;

import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.datastax.driver.core.*;
import java.util.logging.Logger;

public final class CassandraDataControl {
    private final Cluster cluster;
    private final Session session;
    public final String car = "CARS";
    public final String lustre = "LUSTRA";
    public final String patrols = "PATRULS"; // for table with Patruls info
    public final String polygon = "POLYGON";
    private final String dbName = "TABLETS";
    private static CassandraDataControl cassandraDataControl = new CassandraDataControl();
    private final Logger logger = Logger.getLogger( CassandraDataControl.class.toString() );

    public static CassandraDataControl getInstance() { return cassandraDataControl != null ? cassandraDataControl : ( cassandraDataControl = new CassandraDataControl() ); }

    private CassandraDataControl () { ( this.session = ( this.cluster = Cluster.builder().withPort( 9942 ).addContactPoint( "10.254.1.227" ).withProtocolVersion( ProtocolVersion.V4 ).withRetryPolicy( DefaultRetryPolicy.INSTANCE )
                .withSocketOptions( new SocketOptions().setReadTimeoutMillis( 30000 ) ).withLoadBalancingPolicy( new TokenAwarePolicy( DCAwareRoundRobinPolicy.builder().build() ) )
                .withPoolingOptions( new PoolingOptions().setMaxConnectionsPerHost( HostDistance.LOCAL, 1024 ).setMaxRequestsPerConnection( HostDistance.REMOTE, 256 ).setPoolTimeoutMillis( 60000 ) ).build() ).connect() )
                .execute( "CREATE KEYSPACE IF NOT EXISTS " + this.dbName + " WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor':1 };" );
        this.session.execute("CREATE TABLE IF NOT EXISTS " + this.dbName + "." + this.patrols + "(passportNumber text, NSF text, object text, PRIMARY KEY( (passportNumber), NSF ) );" ); // the table for patruls
        this.session.execute("""
                CREATE CUSTOM INDEX IF NOT EXISTS patrul_name_idx ON TABLETS.PATRULS(NSF) USING 'org.apache.cassandra.index.sasi.SASIIndex'
                WITH OPTIONS = {
                    'mode': 'CONTAINS',
                    'analyzer_class': 'org.apache.cassandra.index.sasi.analyzer.StandardAnalyzer',
                    'tokenization_enable_stemming': 'true',
                    'tokenization_locale': 'en',
                    'tokenization_skip_stop_words': 'true',
                    'analyzed': 'true',
                    'tokenization_normalize_lowercase': 'true' };""");

        this.session.execute("CREATE TABLE IF NOT EXISTS " + this.dbName + "." + this.polygon + "(id uuid PRIMARY KEY, polygonName text, polygonType text);" ); // the table for polygons
        this.logger.info( "Cassandra is ready" ); }

    public void delete () {
        this.session.close();
        this.cluster.close();
        cassandraDataControl = null;
        this.logger.info( "Cassandra is closed!!!" ); }
}
