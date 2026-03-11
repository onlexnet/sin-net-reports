package sinnet.app.ports.in;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import sinnet.domain.models.EntityId;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.gql.models.ServiceModelGql;

public interface TimeentriesServicePortIn {
    
    List<ServiceModelGql> search(UUID projectId, LocalDate from, LocalDate to, Function<String, CustomerEntityGql> customerMapper);
                                      
    ServiceModelGql getAction(UUID projectId, UUID entityId, Function<String, CustomerEntityGql> customerMapper);
    
    boolean update(EntityId entityId, String customerId, String description, int distance, int duration,
                 String servicemanEmail, String servicemanName, LocalDate whenProvided);

    EntityId newEntry(String requestorEmail, UUID projectId, LocalDate entryDate);

    boolean remove(UUID projectId, UUID entityId, int entityVersion);
}
