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
      version = EntityVersion.New.INSTANCE;
    }
  }

  /**
   * Creates next version of the same entity.
   */
  public ShardedId next() {
    var nextVersion = switch (version) {
      case EntityVersion.New ignored -> new EntityVersion.Existing(1);
      case EntityVersion.Existing v -> new EntityVersion.Existing(v.value() + 1);
    };
    return new ShardedId(projectId, id, nextVersion);
  }

  public static ShardedId of(UUID projectId, UUID id, long version) {
    // after migration from Spring Boot 3.x -> 3.4+ version==0 is not longer accepter in JPA operations when saving new entity.
    return new ShardedId(projectId, id, version <= 0 ? EntityVersion.New.INSTANCE : new EntityVersion.Existing(version));
  }

  public static ShardedId of(Entity<?> entity) {
    return new ShardedId(entity.getProjectId(), entity.getEntityId(), entity.getVersion());
  }

  public static ShardedId anyNew(UUID projectId) {
    return new ShardedId(projectId, UUID.randomUUID(), EntityVersion.New.INSTANCE);
  }

  public static ShardedId anyNew(ProjectId projectId) {
    return anyNew(projectId.getId());
  }
}
