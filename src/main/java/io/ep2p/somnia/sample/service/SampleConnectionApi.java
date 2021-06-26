package io.ep2p.somnia.sample.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ep2p.kademlia.connection.NodeConnectionApi;
import io.ep2p.kademlia.model.FindNodeAnswer;
import io.ep2p.kademlia.model.PingAnswer;
import io.ep2p.kademlia.node.Node;
import io.ep2p.somnia.decentralized.SomniaConnectionInfo;
import io.ep2p.somnia.model.SomniaKey;
import io.ep2p.somnia.model.SomniaValue;
import io.ep2p.somnia.sample.configuration.Address;
import io.ep2p.somnia.sample.domain.BasicResultDto;
import io.ep2p.somnia.sample.domain.FindNodeRequest;
import io.ep2p.somnia.sample.domain.KeyValueDto;
import io.ep2p.somnia.sample.domain.SomniaDTO;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;

public class SampleConnectionApi implements NodeConnectionApi<BigInteger, SomniaConnectionInfo> {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SampleConnectionApi(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }


    private <E, O> O sendRequest(SomniaConnectionInfo somniaConnectionInfo, String address, Node<BigInteger, SomniaConnectionInfo> node, Node<BigInteger, SomniaConnectionInfo> requester, E input, Class<O> outputClass){
        JsonNode jsonNode = input != null ? this.objectMapper.valueToTree(input) : null;

        SomniaDTO somniaDTO = SomniaDTO.builder()
                .node(node)
                .requester(requester)
                .object(jsonNode)
                .build();

        return restTemplate.postForEntity(
                somniaConnectionInfo.getAddress() + address,
                somniaDTO,
                outputClass).getBody();
    }

    @Override
    public PingAnswer<BigInteger> ping(Node<BigInteger, SomniaConnectionInfo> node, Node<BigInteger, SomniaConnectionInfo> contactingNode) {
        return this.sendRequest(contactingNode.getConnectionInfo(), Address.PING, node, null, null, PingAnswer.class);
    }

    @Override
    public void shutdownSignal(Node<BigInteger, SomniaConnectionInfo> node, Node<BigInteger, SomniaConnectionInfo> contactingNode) {
        this.sendRequest(contactingNode.getConnectionInfo(), Address.SHUTDOWN, node, null,null, BasicResultDto.class);
    }

    @Override
    public FindNodeAnswer<BigInteger, SomniaConnectionInfo> findNode(Node<BigInteger, SomniaConnectionInfo> node, Node<BigInteger, SomniaConnectionInfo> contactingNode, BigInteger bigInteger) {
        return this.sendRequest(contactingNode.getConnectionInfo(), Address.FIND, node, null, new FindNodeRequest(bigInteger), FindNodeAnswer.class);
    }

    @Override
    public <K, V> void storeAsync(Node<BigInteger, SomniaConnectionInfo> caller, Node<BigInteger, SomniaConnectionInfo> requester, Node<BigInteger, SomniaConnectionInfo> contactingNode, K key, V value) {
        this.sendRequest(contactingNode.getConnectionInfo(), Address.STORE, caller, requester, KeyValueDto.<K, V>builder().value((SomniaValue) value).key((SomniaKey) key).build(), BasicResultDto.class);
    }

    @Override
    public <K> void getRequest(Node<BigInteger, SomniaConnectionInfo> caller, Node<BigInteger, SomniaConnectionInfo> requester, Node<BigInteger, SomniaConnectionInfo> contactingNode, K key) {
        this.sendRequest(contactingNode.getConnectionInfo(), Address.GET, caller, requester, KeyValueDto.<K, Void>builder().key((SomniaKey) key).build(), BasicResultDto.class);
    }

    @Override
    public <K, V> void sendGetResults(Node<BigInteger, SomniaConnectionInfo> caller, Node<BigInteger, SomniaConnectionInfo> requester, K key, V value) {
        this.sendRequest(requester.getConnectionInfo(), Address.GET_RESULT, caller, requester, KeyValueDto.<K, V>builder().key((SomniaKey) key).value((SomniaValue) value).build(), BasicResultDto.class);
    }

    @Override
    public <K> void sendStoreResults(Node<BigInteger, SomniaConnectionInfo> caller, Node<BigInteger, SomniaConnectionInfo> requester, K key, boolean success) {
        this.sendRequest(requester.getConnectionInfo(), Address.STORE_RESULT, caller, requester, KeyValueDto.<K, Void>builder().key((SomniaKey) key).success(success).build(), BasicResultDto.class);
    }
}
