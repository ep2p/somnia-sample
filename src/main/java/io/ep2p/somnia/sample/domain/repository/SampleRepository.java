package io.ep2p.somnia.sample.domain.repository;

import io.ep2p.somnia.config.dynamic.DynamicRepository;
import io.ep2p.somnia.sample.domain.DataModel;
import io.ep2p.somnia.sample.domain.SampleSomniaEntity;
import io.ep2p.somnia.storage.SomniaRepository;

@DynamicRepository(through = SampleSomniaEntity.class)
public interface SampleRepository extends SomniaRepository<DataModel> {
}
