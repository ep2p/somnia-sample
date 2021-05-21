package io.ep2p.somnia.sample.configuration;

import com.github.ep2p.kademlia.connection.ConnectionInfo;
import com.github.ep2p.kademlia.connection.NodeConnectionApi;
import com.github.ep2p.kademlia.table.BigIntegerRoutingTable;
import com.github.ep2p.kademlia.table.Bucket;
import com.github.ep2p.kademlia.table.RoutingTable;
import io.ep2p.somnia.sample.domain.SampleConnectionInfo;
import io.ep2p.somnia.sample.service.SampleConnectionApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.math.BigInteger;

@Configuration
@EnableMongoRepositories(basePackages = "io.ep2p.somnia.sample.domain")
public class ApplicationConfiguration {
    private final int nodeId;
    private final int port;
    private final String host;

    public ApplicationConfiguration(@Value("${nodeId}") int nodeId, @Value("${server.port}") int port, @Value("${server.host}") String host) {
        this.nodeId = nodeId;
        this.port = port;
        this.host = host;
    }

    @Bean("somniaNodeId")
    public BigInteger nodeId(){
        return BigInteger.valueOf((long) this.nodeId);
    }

    @Bean("routingTable")
    @DependsOn("somniaNodeId")
    public RoutingTable<BigInteger, ConnectionInfo, Bucket<BigInteger, ConnectionInfo>> routingTable(BigInteger somniaNodeId){
        return new BigIntegerRoutingTable<>(somniaNodeId);
    }

    @Bean("connectionInfo")
    public ConnectionInfo connectionInfo(){
        return SampleConnectionInfo.builder()
                .address("http://"+ this.host + ":" + this.port)
                .build();
    }

    @Bean("nodeConnectionApi")
    public NodeConnectionApi<BigInteger, ConnectionInfo> nodeConnectionApi(){
        return new SampleConnectionApi();
    }

}
