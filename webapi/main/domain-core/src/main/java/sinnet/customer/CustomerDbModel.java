package sinnet.customer;

import java.util.UUID;

import lombok.Data;

@Data
public class CustomerDbModel {
    private UUID entityId;

    private String name;
}
