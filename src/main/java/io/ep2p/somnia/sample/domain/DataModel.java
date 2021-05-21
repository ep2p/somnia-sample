package io.ep2p.somnia.sample.domain;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataModel implements Serializable {
    private String name;
}
