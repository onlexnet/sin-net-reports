package sinnet.models;

import java.util.UUID;

public interface EntityValue<T> {
    default Entity<T> withId(UUID projectId, UUID entityId, int version) {
        return new Entity<>(projectId, entityId, version, (T) this);
    }
}
