package sinnet.models;

import java.util.UUID;

/**
 * TBD.
 */
public record ShardedId(UUID projectId, UUID id, EntityVersion version) {

  /**
   * Makes sure that version is never null.
   */
  public ShardedId {
    if (version == null) {
      version = EntityVersion.Reserved.INSTANCE;
    }
  }

  /**
   * Creates next version of the same entity.
   */
  public ShardedId next() {
    var nextVersion = switch (version) {
      case EntityVersion.Reserved ignored -> new EntityVersion.Existing(0);
      case EntityVersion.Existing v -> new EntityVersion.Existing(v.value() + 1);
    };
    return new ShardedId(projectId, id, nextVersion);
  }

  public static ShardedId of(UUID projectId, UUID id, long version) {
    return new ShardedId(projectId, id, version < 0 ? EntityVersion.Reserved.INSTANCE : new EntityVersion.Existing(version));
  }

  public static ShardedId of(Entity<?> entity) {
    return new ShardedId(entity.getProjectId(), entity.getEntityId(), entity.getVersion());
  }

  public static ShardedId reserved(UUID projectId) {
    return new ShardedId(projectId, UUID.randomUUID(), EntityVersion.Reserved.INSTANCE);
  }
}
