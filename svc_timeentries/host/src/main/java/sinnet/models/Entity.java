package sinnet.models;

import java.util.UUID;

import lombok.Value;

/**
 * TBD.
 */
@Value
public class Entity<V> {

  @Deprecated
  private UUID projectId;
  @Deprecated
  private UUID entityId;
  @Deprecated
  private EntityVersion version;

  public ShardedId getId() {
    return new ShardedId(projectId, entityId, version);
  }

  private V value;
}
