package sinnet.models;

import java.util.UUID;

/**
 * TBD.
 */
public interface EntityValue<T> {

  default Entity<T> withId(UUID projectId, UUID entityId, EntityVersion version) {
    return new Entity<>(projectId, entityId, version, (T) this);
  }

  default Entity<T> withId(ShardedId eid) {
    return withId(eid.projectId(), eid.id(), eid.version());
  }
}
