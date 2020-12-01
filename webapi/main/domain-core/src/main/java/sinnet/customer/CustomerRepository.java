package sinnet.customer;

import java.util.UUID;

import io.vertx.core.Future;
import sinnet.models.CustomerValue;
import sinnet.models.Entity;
import sinnet.models.EntityId;

interface CustomerRepository {
    Future<Boolean> save(EntityId id, CustomerValue entity);
    Future<Entity<CustomerValue>> get(EntityId id);

    /* Returns success with found Entity, otherwise failed future. */
    Future<Entity<CustomerValue>> get(UUID id);

    Future<Iterable<Entity<CustomerValue>>> list();
}
