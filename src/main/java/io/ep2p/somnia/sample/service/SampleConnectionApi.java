package io.ep2p.somnia.sample.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ep2p.kademlia.connection.ConnectionInfo;
import com.github.ep2p.kademlia.connection.NodeConnectionApi;
import com.github.ep2p.kademlia.model.FindNodeAnswer;
import com.github.ep2p.kademlia.model.PingAnswer;
import com.github.ep2p.kademlia.node.Node;
import io.ep2p.somnia.model.SomniaKey;
import io.ep2p.somnia.model.SomniaValue;
import io.ep2p.somnia.sample.configuration.Address;
import io.ep2p.somnia.sample.domain.KeyValueDto;
import io.ep2p.somnia.sample.domain.SampleConnectionInfo;
import io.ep2p.somnia.sample.domain.SomniaDTO;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;

public class SampleConnectionApi implements NodeConnectionApi<BigInteger, ConnectionInfo> {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    private <E, O> O sendRequest(ConnectionInfo sampleConnectionInfo, String address, Node<BigInteger, ConnectionInfo> node, E input, Class<O> outputClass){
        SampleConnectionInfo sampleConnectionInfo1 = (SampleConnectionInfo) sampleConnectionInfo;
        return restTemplate.postForEntity(
                sampleConnectionInfo1.getAddress() + address,
                SomniaDTO.builder()
                    .node( node)
                    .object(input != null ? this.objectMapper.valueToTree(input) : null)
                    .build(),
                outputClass).getBody();
    }

    @Override
    public PingAnswer<BigInteger> ping(Node<BigInteger, ConnectionInfo> node, Node<BigInteger, ConnectionInfo> contactingNode) {
        return this.sendRequest(contactingNode.getConnectionInfo(), Address.PING, node, null, PingAnswer.class);
    }

    @Override
    public void shutdownSignal(Node<BigInteger, ConnectionInfo> node, Node<BigInteger, ConnectionInfo> contactingNode) {
        this.sendRequest(contactingNode.getConnectionInfo(), Address.SHUTDOWN, node, null, String.class);
    }

    @Override
    public FindNodeAnswer<BigInteger, ConnectionInfo> findNode(Node<BigInteger, ConnectionInfo> node, Node<BigInteger, ConnectionInfo> contactingNode, BigInteger bigInteger) {
        return this.sendRequest(contactingNode.getConnectionInfo(), Address.FIND, node, bigInteger, FindNodeAnswer.class);
    }

    @Override
    public <K, V> void storeAsync(Node<BigInteger, ConnectionInfo> caller, Node<BigInteger, ConnectionInfo> requester, Node<BigInteger, ConnectionInfo> contactingNode, K key, V value) {
        this.sendRequest(contactingNode.getConnectionInfo(), Address.STORE, caller, KeyValueDto.<K, V>builder().value((SomniaValue) value).key((SomniaKey) key).build(), String.class);
    }

    @Override
    public <K> void getRequest(Node<BigInteger, ConnectionInfo> caller, Node<BigInteger, ConnectionInfo> requester, Node<BigInteger, ConnectionInfo> contactingNode, K key) {
        this.sendRequest(contactingNode.getConnectionInfo(), Address.GET, caller, KeyValueDto.<K, Void>builder().key((SomniaKey) key).build(), String.class);
    }

    @Override
    public <K, V> void sendGetResults(Node<BigInteger, ConnectionInfo> caller, Node<BigInteger, ConnectionInfo> requester, K key, V value) {
        this.sendRequest(requester.getConnectionInfo(), Address.GET_RESULT, caller, KeyValueDto.<K, V>builder().key((SomniaKey) key).value((SomniaValue) value).build(), String.class);
    }

    @Override
    public <K> void sendStoreResults(Node<BigInteger, ConnectionInfo> caller, Node<BigInteger, ConnectionInfo> requester, K key, boolean success) {
        this.sendRequest(requester.getConnectionInfo(), Address.STORE_RESULT, caller, KeyValueDto.<K, Void>builder().key((SomniaKey) key).success(success).build(), String.class);
    }
}
