package sinnet.customer;

import java.util.UUID;

import io.vertx.core.Future;
import sinnet.models.CustomerValue;
import sinnet.models.Entity;
import sinnet.models.EntityId;

interface CustomerRepository {

    /**
     * Saves a new version of Entity identified by given eid.
     * <p>
     * @return new EID for just stored entity.
     */
    Future<EntityId> save(EntityId eid, CustomerValue value);

    Future<Entity<CustomerValue>> get(EntityId id);

    /* Returns success with found Entity, otherwise failed future. */
    Future<Entity<CustomerValue>> get(UUID projectId, UUID id);

    Future<Iterable<Entity<CustomerValue>>> list(UUID projectId);
}
