package sinnet.models;

import java.util.UUID;

public interface EntityValue<T> {
    default Entity<T> withId(UUID entityId, int version) {
        return new Entity<>(entityId, version, (T) this);
    }
}
