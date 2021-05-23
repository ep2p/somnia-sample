package io.ep2p.somnia.sample.domain;

import io.ep2p.somnia.model.SomniaKey;
import io.ep2p.somnia.model.SomniaValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeyValueDto {
    private SomniaKey key;
    private SomniaValue value;
    private boolean success;
}
