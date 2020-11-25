package sinnet;

import java.util.UUID;

import lombok.Value;

@Value
public class SomeEntity implements Entity {
    private UUID entityId;
    private int entityVersion;
}
