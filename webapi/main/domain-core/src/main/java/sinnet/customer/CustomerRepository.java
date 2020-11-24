package sinnet.customer;

import io.vavr.control.Option;
import io.vertx.core.Future;
import sinnet.models.CustomerValue;
import sinnet.models.EntityId;

interface CustomerRepository {
    Future<Boolean> save(EntityId id, CustomerValue entity);
    Future<Option<CustomerValue>> get(EntityId id);
}
