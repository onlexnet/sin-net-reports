package sinnet.models;

import java.util.UUID;

public interface EntityValue<T> {
    default Entity<T> withId(UUID entityId) {
        return new Entity<>(entityId, (T) this);
    }
}
