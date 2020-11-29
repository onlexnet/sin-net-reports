package sinnet.models;

import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public final class EntityId {
    @Getter
    private final UUID id;
    @Getter
    private final int version;

    private EntityId(UUID id, int version) {
        this.id = id;
        this.version = version;
    }

    public static EntityId anyNew() {
        return new EntityId(UUID.randomUUID(), 1);
    }
}
