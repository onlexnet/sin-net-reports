package sinnet.customer;

import java.util.UUID;

import io.vavr.collection.List;
import io.vertx.core.Future;
import lombok.Value;
import sinnet.models.CustomerAuthorization;
import sinnet.models.CustomerValue;
import sinnet.models.EntityId;

interface CustomerRepository {

    /**
     * Saves a new version of Entity identified by given eid.
     * <p>
     * @return new EID for just stored entity.
     */
    Future<EntityId> write(EntityId eid, CustomerValue value, CustomerAuthorization[] auth);

    Future<CustomerModel> get(EntityId id);

    /* Returns success with found Entity, otherwise failed future. */
    Future<CustomerModel> get(UUID projectId, UUID id);

    Future<List<CustomerModel>> list(UUID projectId);

    @Value
    class CustomerModel {
        private EntityId id;
        private CustomerValue value;
        private CustomerAuthorization[] authorisations;
    }
}
