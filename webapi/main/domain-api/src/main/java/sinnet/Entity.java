package sinnet;

import java.util.UUID;

import lombok.Value;

@Value
public class Entity<VALUE> {
    private UUID entityId;
    private VALUE value;
}
