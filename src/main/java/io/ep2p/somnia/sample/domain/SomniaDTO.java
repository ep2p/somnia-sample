package io.ep2p.somnia.sample.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.ep2p.kademlia.connection.ConnectionInfo;
import com.github.ep2p.kademlia.node.Node;
import io.ep2p.somnia.decentralized.SomniaConnectionInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SomniaDTO {
    private Node<BigInteger, SomniaConnectionInfo> node;
    private Node<BigInteger, SomniaConnectionInfo> requester;
    private JsonNode object;
}
