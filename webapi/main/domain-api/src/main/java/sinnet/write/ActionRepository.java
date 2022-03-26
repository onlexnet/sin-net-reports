package sinnet.write;

import io.vertx.core.Future;
import sinnet.models.ActionValue;
import sinnet.models.EntityId;

/**
 * Basic operations on store / get an entity from database.
 */
// TODO Move behind bus
public interface ActionRepository {

  Future<Boolean> save(EntityId entityId, ActionValue entity);

  Future<EntityId> update(EntityId id, ActionValue entity);

  Future<Boolean> remove(EntityId id);
}
