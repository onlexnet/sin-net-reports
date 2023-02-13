package sinnet;

import java.util.UUID;

import sinnet.customer.CustomerRepository;
import sinnet.models.CustomerContact;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerValue;
import sinnet.models.EntityId;
import sinnet.models.Name;

public class Given {
    public static EntityId customer(UUID projectId, CustomerRepository repository) {
        var id = UUID.randomUUID();
        var eid = EntityId.of(projectId, id, 0);
        var value = CustomerValue.builder()
            .customerName(Name.of("Some customer [" + UUID.randomUUID() + "]"))
            .build();
        var secrets = new CustomerSecret[0];
        var secretsEx = new CustomerSecretEx[0];
        var contacts = new CustomerContact[0];
        var result = Sync.of(() -> repository.write(eid, value, secrets, secretsEx, contacts)).get();
        return result;
    }
}
