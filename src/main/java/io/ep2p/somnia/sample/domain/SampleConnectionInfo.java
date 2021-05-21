package io.ep2p.somnia.sample.domain;

import com.github.ep2p.kademlia.connection.ConnectionInfo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SampleConnectionInfo implements ConnectionInfo {
    private String address;
}
