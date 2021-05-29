package io.ep2p.somnia.sample.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ep2p.kademlia.exception.BootstrapException;
import com.github.ep2p.kademlia.model.FindNodeAnswer;
import com.github.ep2p.kademlia.model.PingAnswer;
import com.github.ep2p.kademlia.node.external.BigIntegerExternalNode;
import io.ep2p.somnia.decentralized.SomniaConnectionInfo;
import io.ep2p.somnia.decentralized.SomniaKademliaSyncRepositoryNode;
import io.ep2p.somnia.sample.configuration.Address;
import io.ep2p.somnia.sample.domain.BasicResultDto;
import io.ep2p.somnia.sample.domain.FindNodeRequest;
import io.ep2p.somnia.sample.domain.KeyValueDto;
import io.ep2p.somnia.sample.domain.SomniaDTO;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RestController
public class SomniaController {
    private final SomniaKademliaSyncRepositoryNode somniaKademliaSyncRepositoryNode;
    private final ObjectMapper objectMapper;
    private final Executor executor = Executors.newSingleThreadExecutor();

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
    public BasicResultDto onShutdown(@RequestBody SomniaDTO somniaDTO){
        somniaKademliaSyncRepositoryNode.onShutdownSignal(somniaDTO.getNode());
        return new BasicResultDto();
    }

    @SneakyThrows
    @PostMapping(Address.GET)
    public BasicResultDto onGet(@RequestBody SomniaDTO somniaDTO){
        executor.execute(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                somniaKademliaSyncRepositoryNode.onGetRequest(somniaDTO.getNode(), somniaDTO.getRequester(), objectMapper.readValue(somniaDTO.getObject().toString(), KeyValueDto.class).getKey());
            }
        });
        return new BasicResultDto();
    }

    @SneakyThrows
    @PostMapping(Address.FIND)
    public FindNodeAnswer<BigInteger, SomniaConnectionInfo> onFind(@RequestBody SomniaDTO somniaDTO){
        return somniaKademliaSyncRepositoryNode.onFindNode(somniaDTO.getNode(), objectMapper.readValue(somniaDTO.getObject().toString(), FindNodeRequest.class).getId());
    }

    @SneakyThrows
    @PostMapping(Address.STORE)
    public BasicResultDto onStore(@RequestBody SomniaDTO somniaDTO){
        KeyValueDto keyValueDto = objectMapper.readValue(somniaDTO.getObject().toString(), KeyValueDto.class);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                somniaKademliaSyncRepositoryNode.onStoreRequest(somniaDTO.getNode(), somniaDTO.getRequester(), keyValueDto.getKey(), keyValueDto.getValue());
            }
        });
        return new BasicResultDto();
    }

    @PostMapping(Address.GET_RESULT)
    @SneakyThrows
    public BasicResultDto onGetResult(@RequestBody SomniaDTO somniaDTO){
        KeyValueDto keyValueDto = objectMapper.readValue(somniaDTO.getObject().toString(), KeyValueDto.class);
        somniaKademliaSyncRepositoryNode.onGetResult(somniaDTO.getNode(), keyValueDto.getKey(), keyValueDto.getValue());
        return new BasicResultDto();
    }

    @PostMapping(Address.STORE_RESULT)
    @SneakyThrows
    public BasicResultDto onStoreResult(@RequestBody SomniaDTO somniaDTO){
        KeyValueDto keyValueDto = objectMapper.readValue(somniaDTO.getObject().toString(), KeyValueDto.class);
        somniaKademliaSyncRepositoryNode.onStoreResult(somniaDTO.getNode(), keyValueDto.getKey(), keyValueDto.isSuccess());
        return new BasicResultDto();
    }

    @PostMapping(Address.BOOTSTRAP)
    public BasicResultDto bootstrap(@RequestBody SomniaConnectionInfo connectionInfo, @PathVariable("id") long id) throws BootstrapException {
        BigIntegerExternalNode<SomniaConnectionInfo> externalNode = new BigIntegerExternalNode<>();
        externalNode.setConnectionInfo(connectionInfo);
        externalNode.setId(BigInteger.valueOf(id));
        somniaKademliaSyncRepositoryNode.bootstrap(externalNode);
        return new BasicResultDto();
    }

}
