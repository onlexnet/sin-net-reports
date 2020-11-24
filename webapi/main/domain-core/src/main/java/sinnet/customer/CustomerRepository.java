package sinnet.customer;

import java.util.UUID;

import io.vertx.core.Future;
import sinnet.models.CustomerValue;

interface CustomerRepository {
    Future<Boolean> save(UUID entityId, int version, CustomerValue entity);
}
