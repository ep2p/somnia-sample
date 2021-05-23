package io.ep2p.somnia.sample.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ep2p.kademlia.connection.ConnectionInfo;
import com.github.ep2p.kademlia.model.FindNodeAnswer;
import com.github.ep2p.kademlia.model.PingAnswer;
import io.ep2p.somnia.decentralized.SomniaKademliaSyncRepositoryNode;
import io.ep2p.somnia.sample.configuration.Address;
import io.ep2p.somnia.sample.domain.KeyValueDto;
import io.ep2p.somnia.sample.domain.SomniaDTO;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
public class SomniaController {
    private final SomniaKademliaSyncRepositoryNode somniaKademliaSyncRepositoryNode;
    private final ObjectMapper objectMapper;

    public SomniaController(SomniaKademliaSyncRepositoryNode somniaKademliaSyncRepositoryNode, ObjectMapper objectMapper) {
        this.somniaKademliaSyncRepositoryNode = somniaKademliaSyncRepositoryNode;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @PostMapping(Address.PING)
    public PingAnswer<BigInteger> onPing(@RequestBody SomniaDTO somniaDTO){
        return somniaKademliaSyncRepositoryNode.onPing(somniaDTO.getNode());
    }

    @PostMapping(Address.SHUTDOWN)
    public String onShutdown(@RequestBody SomniaDTO somniaDTO){
        somniaKademliaSyncRepositoryNode.onShutdownSignal(somniaDTO.getNode());
        return "OK";
    }

    @SneakyThrows
    @PostMapping(Address.FIND)
    public FindNodeAnswer<BigInteger, ConnectionInfo> onFind(@RequestBody SomniaDTO somniaDTO){
        return somniaKademliaSyncRepositoryNode.onFindNode(somniaDTO.getNode(), objectMapper.readValue(somniaDTO.getObject().toString(), BigInteger.class));
    }

    @SneakyThrows
    @PostMapping(Address.STORE)
    public String onStore(@RequestBody SomniaDTO somniaDTO){
        KeyValueDto keyValueDto = objectMapper.readValue(somniaDTO.getObject().toString(), KeyValueDto.class);
        somniaKademliaSyncRepositoryNode.onStoreRequest(somniaDTO.getNode(), somniaDTO.getRequester(), keyValueDto.getKey(), keyValueDto.getValue());
        return "OK";
    }

    @PostMapping(Address.GET_RESULT)
    @SneakyThrows
    public String onGetResult(@RequestBody SomniaDTO somniaDTO){
        KeyValueDto keyValueDto = objectMapper.readValue(somniaDTO.getObject().toString(), KeyValueDto.class);
        somniaKademliaSyncRepositoryNode.onGetResult(somniaDTO.getNode(), keyValueDto.getKey(), keyValueDto.getValue());
        return "OK";
    }

    @PostMapping(Address.STORE_RESULT)
    @SneakyThrows
    public String onStoreResult(@RequestBody SomniaDTO somniaDTO){
        KeyValueDto keyValueDto = objectMapper.readValue(somniaDTO.getObject().toString(), KeyValueDto.class);
        somniaKademliaSyncRepositoryNode.onStoreResult(somniaDTO.getNode(), keyValueDto.getKey(), keyValueDto.isSuccess());
        return "OK";
    }

}
