package sinnet;

import java.time.LocalDate;
import java.util.UUID;

import io.vavr.collection.Array;
import io.vertx.core.Future;
import sinnet.models.ActionValue;
import sinnet.models.Entity;
import sinnet.models.EntityId;

public interface ActionRepository {

    Future<Boolean> save(EntityId entityId, ActionValue entity);

    Future<EntityId> update(EntityId id, ActionValue entity);

    Future<Array<Entity<ActionValue>>> find(UUID projectId, LocalDate from, LocalDate to);

    Future<Entity<ActionValue>> find(UUID projectId, UUID entityId);
}
