package io.ep2p.somnia.sample.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.ep2p.kademlia.connection.ConnectionInfo;
import com.github.ep2p.kademlia.connection.NodeConnectionApi;
import com.github.ep2p.kademlia.node.external.BigIntegerExternalNode;
import com.github.ep2p.kademlia.node.external.ExternalNode;
import com.github.ep2p.kademlia.table.BigIntegerRoutingTable;
import com.github.ep2p.kademlia.table.Bucket;
import com.github.ep2p.kademlia.table.RoutingTable;
import io.ep2p.somnia.decentralized.SomniaConnectionInfo;
import io.ep2p.somnia.sample.service.SampleConnectionApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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

    public static class ExternalNodeSerializer extends JsonSerializer<ExternalNode> {

        @Override
        public void serialize(ExternalNode externalNode, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
            jgen.writeStartObject();
            jgen.writeNumberField("id", (BigInteger) externalNode.getId());
            jgen.writeNumberField("distance", (BigInteger) externalNode.getDistance());
            jgen.writeObjectField("connectionInfo", externalNode.getConnectionInfo());
            jgen.writeEndObject();
        }
    }

    @Bean({"objectMapper", "somniaObjectMapper"})
    @Primary
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addSerializer(ExternalNode.class, new ExternalNodeSerializer());
        module.addDeserializer(ExternalNode.class, new JsonDeserializer<ExternalNode<BigInteger, SomniaConnectionInfo>>() {
            @Override
            public ExternalNode<BigInteger, SomniaConnectionInfo> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
                JsonNode jsonNode = deserializationContext.readTree(jsonParser);
                SomniaConnectionInfo connectionInfo = objectMapper.readValue(jsonNode.get("connectionInfo").toString(), SomniaConnectionInfo.class);
                BigIntegerExternalNode<SomniaConnectionInfo> bigIntegerExternalNode = new BigIntegerExternalNode<>();
                bigIntegerExternalNode.setConnectionInfo(connectionInfo);
                bigIntegerExternalNode.setId(jsonNode.get("id").bigIntegerValue());
                bigIntegerExternalNode.setDistance(jsonNode.get("distance").bigIntegerValue());
                return bigIntegerExternalNode;
            }
        });
        objectMapper.registerModules(module);
        return objectMapper;
    }

    @Bean("nodeConnectionApi")
    @DependsOn("somniaObjectMapper")
    public NodeConnectionApi<BigInteger, SomniaConnectionInfo> nodeConnectionApi(ObjectMapper somniaObjectMapper){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, createMappingJacksonHttpMessageConverter(somniaObjectMapper));
        return new SampleConnectionApi(restTemplate, somniaObjectMapper);
    }

    private MappingJackson2HttpMessageConverter createMappingJacksonHttpMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }

}
