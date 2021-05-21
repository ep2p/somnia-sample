package io.ep2p.somnia.sample.domain;

import io.ep2p.somnia.annotation.SomniaDocument;
import io.ep2p.somnia.model.EntityType;
import io.ep2p.somnia.model.SomniaEntity;

@SomniaDocument(type = EntityType.HIT)
public class SampleSomniaEntity extends SomniaEntity<DataModel> {

}
