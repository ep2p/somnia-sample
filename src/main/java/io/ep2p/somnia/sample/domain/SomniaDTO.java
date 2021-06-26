package io.ep2p.somnia.sample.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import io.ep2p.kademlia.node.Node;
import io.ep2p.somnia.decentralized.SomniaConnectionInfo;
import lombok.*;

import java.math.BigInteger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class SomniaDTO {
    private Node<BigInteger, SomniaConnectionInfo> node;
    private Node<BigInteger, SomniaConnectionInfo> requester;
    private JsonNode object;
}
