package io.ep2p.somnia.sample.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ep2p.kademlia.exception.StoreException;
import com.github.ep2p.kademlia.model.GetAnswer;
import com.github.ep2p.kademlia.model.StoreAnswer;
import io.ep2p.somnia.decentralized.SomniaKademliaSyncRepositoryNode;
import io.ep2p.somnia.model.SomniaKey;
import io.ep2p.somnia.model.SomniaValue;
import io.ep2p.somnia.sample.domain.DataModel;
import io.ep2p.somnia.sample.domain.SampleSomniaEntity;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.ep2p.kademlia.model.GetAnswer.Result.FOUND;

@Service
public class StorageService {
    private final SomniaKademliaSyncRepositoryNode somniaKademliaSyncRepositoryNode;
    private final ObjectMapper objectMapper;

    public StorageService(SomniaKademliaSyncRepositoryNode somniaKademliaSyncRepositoryNode, ObjectMapper objectMapper) {
        this.somniaKademliaSyncRepositoryNode = somniaKademliaSyncRepositoryNode;
        this.objectMapper = objectMapper;
    }


    @SneakyThrows
    public String store(long id, DataModel dataModel) throws StoreException {
        SomniaKey somniaKey = SomniaKey.builder()
                .key(BigInteger.valueOf(id))
                .name(SampleSomniaEntity.class.getName())
                .build();
        StoreAnswer<BigInteger, SomniaKey> storeAnswer = somniaKademliaSyncRepositoryNode.store(somniaKey, SomniaValue.builder()
                .data(objectMapper.valueToTree(dataModel))
                .build(), 5, TimeUnit.SECONDS);
        return storeAnswer.getResult().toString();
    }

    @SneakyThrows
    public DataModel get(long id){
        SomniaKey somniaKey = SomniaKey.builder()
                .key(BigInteger.valueOf(id))
                .name(SampleSomniaEntity.class.getName())
                .build();
        GetAnswer<BigInteger, SomniaKey, SomniaValue> getAnswer = somniaKademliaSyncRepositoryNode.get(somniaKey, 5, TimeUnit.SECONDS);
        if (getAnswer.getResult() == FOUND) {
            JsonNode jsonNode = getAnswer.getValue().getData();
            List<DataModel> sampleDataList = this.objectMapper.readValue(
                    jsonNode.toString(), new TypeReference<List<DataModel>>(){}
            );
            return sampleDataList.get(0);
        }
        return DataModel.builder().build();
    }
}
