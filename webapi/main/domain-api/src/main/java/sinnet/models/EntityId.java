package sinnet.models;

import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public final class EntityId {
    @Getter
    private final UUID projectId;
    @Getter
    private final UUID id;
    @Getter
    private final int version;

    private EntityId(UUID projectId, UUID id, int version) {
        this.projectId = projectId;
        this.id = id;
        this.version = version;
    }

    public static EntityId of(UUID projectId, UUID id, int version) {
        return new EntityId(projectId, id, version);
    }

    public static EntityId anyNew(UUID projectId) {
        return new EntityId(projectId, UUID.randomUUID(), 0);
    }
}
