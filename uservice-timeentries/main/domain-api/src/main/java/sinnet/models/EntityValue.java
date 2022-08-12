package sinnet.models;

import java.util.UUID;

public interface EntityValue<T> {

  default Entity<T> withId(UUID projectId, UUID entityId, long version) {
    return new Entity<>(projectId, entityId, version, (T) this);
  }

  default Entity<T> withId(EntityId eid) {
    return withId(eid.getProjectId(), eid.getId(), eid.getVersion());
  }
}
