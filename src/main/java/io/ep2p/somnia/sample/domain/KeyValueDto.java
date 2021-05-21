package io.ep2p.somnia.sample.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeyValueDto<K, V> {
    private K key;
    private V value;
    private boolean success;
}
