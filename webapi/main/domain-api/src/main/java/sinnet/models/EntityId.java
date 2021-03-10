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
  private int version;

  public static EntityId of(UUID projectId, UUID id, int version) {
    return new EntityId(projectId, id, version);
  }

  public static EntityId anyNew(UUID projectId) {
    return new EntityId(projectId, UUID.randomUUID(), 0);
  }
}
