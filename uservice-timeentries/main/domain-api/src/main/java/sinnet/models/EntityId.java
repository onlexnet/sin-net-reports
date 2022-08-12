package sinnet.models;

import java.util.UUID;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder(toBuilder = true)
public final class EntityId {
  private UUID projectId;
  private UUID id;
  private long version;

  public static EntityId of(UUID projectId, UUID id, long version) {
    return new EntityId(projectId, id, version);
  }

  public static EntityId of(Entity<?> entity) {
    return new EntityId(entity.getProjectId(), entity.getEntityId(), entity.getVersion());
  }

  public static EntityId anyNew(UUID projectId) {
    return new EntityId(projectId, UUID.randomUUID(), 0);
  }

  public static EntityId anyNew(ProjectId projectId) {
    return anyNew(projectId.getId());
  }
}
