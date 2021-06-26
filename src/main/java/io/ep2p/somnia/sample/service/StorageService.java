package io.ep2p.somnia.sample.service;

import io.ep2p.kademlia.exception.StoreException;
import io.ep2p.somnia.model.RepositoryResponse;
import io.ep2p.somnia.sample.domain.DataModel;
import io.ep2p.somnia.sample.domain.repository.SampleRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class StorageService {
    private final SampleRepository sampleRepository;

    @Autowired
    public StorageService(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    public String store(long id, DataModel dataModel) {
        RepositoryResponse<DataModel> response = sampleRepository.save(BigInteger.valueOf(id), dataModel);
        return response.getNode().toString();
    }

    public DataModel get(long id){
        RepositoryResponse<DataModel> response = sampleRepository.findOne(BigInteger.valueOf(id));
        if (response.isSuccess()) {
            return response.getResult();
        }
        return DataModel.builder().build();
    }
}
