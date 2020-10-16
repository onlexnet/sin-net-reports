package sinnet.models;

import java.util.UUID;

import lombok.Value;

@Value
public class Entity<VALUE> {
    private UUID entityId;
    private int version;
    private VALUE value;
}
