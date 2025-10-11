package sinnet.write;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import sinnet.models.ActionValue;
import sinnet.models.Entity;
import sinnet.models.ShardedId;

/**
 * Basic operations on store / get an entity from database.
 */
public interface ActionRepositoryEx {

  ShardedId save(UUID projectId, UUID id, ActionValue entity);

  ShardedId update1(Entity<ActionValue> entity);

  Boolean remove1(ShardedId id);

  List<Entity<ActionValue>> list(UUID projectId, LocalDate from, LocalDate to);

  Entity<ActionValue> get(UUID projectId, UUID eid);
}
