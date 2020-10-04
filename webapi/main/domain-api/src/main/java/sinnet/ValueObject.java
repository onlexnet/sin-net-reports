package sinnet;

import java.util.UUID;

public interface ValueObject<VALUE> {
    default Entity<VALUE> withId(UUID entityId) {
        return new Entity<>(entityId, (VALUE) this);
    }
}
