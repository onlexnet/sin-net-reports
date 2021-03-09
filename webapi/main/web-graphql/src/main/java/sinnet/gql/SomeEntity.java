package sinnet.gql;

import java.util.UUID;

import lombok.Value;

@Value
public class SomeEntity implements Entity {
    private UUID projectId;
    private UUID entityId;
    private int entityVersion;
}
