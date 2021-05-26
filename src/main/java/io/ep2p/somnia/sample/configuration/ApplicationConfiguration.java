package io.ep2p.somnia.sample.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ep2p.kademlia.connection.ConnectionInfo;
import com.github.ep2p.kademlia.connection.NodeConnectionApi;
import com.github.ep2p.kademlia.table.BigIntegerRoutingTable;
import com.github.ep2p.kademlia.table.Bucket;
import com.github.ep2p.kademlia.table.RoutingTable;
import io.ep2p.somnia.decentralized.SomniaConnectionInfo;
import io.ep2p.somnia.sample.service.SampleConnectionApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

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
    public RoutingTable<BigInteger, SomniaConnectionInfo, Bucket<BigInteger, SomniaConnectionInfo>> routingTable(BigInteger somniaNodeId){
        return new BigIntegerRoutingTable<>(somniaNodeId);
    }

    @Bean("somniaConnectionInfo")
    public ConnectionInfo somniaConnectionInfo(){
        return new SomniaConnectionInfo("http://"+ this.host + ":" + this.port);
    }

    @Bean("nodeConnectionApi")
    @DependsOn("objectMapper")
    public NodeConnectionApi<BigInteger, SomniaConnectionInfo> nodeConnectionApi(ObjectMapper objectMapper){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, createMappingJacksonHttpMessageConverter(objectMapper));
        return new SampleConnectionApi(restTemplate, objectMapper);
    }

    private MappingJackson2HttpMessageConverter createMappingJacksonHttpMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }

}
