package sinnet.customer;

import io.vertx.core.Future;
import sinnet.models.CustomerValue;
import sinnet.models.EntityId;

interface CustomerRepository {
    Future<Boolean> save(EntityId id, CustomerValue entity);
}
