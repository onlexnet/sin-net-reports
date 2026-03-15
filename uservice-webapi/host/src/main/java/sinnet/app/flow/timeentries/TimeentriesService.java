package sinnet.app.flow.timeentries;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.TimeentriesServicePortIn;
import sinnet.app.ports.out.ActionsGrpcPortOut;
import sinnet.domain.models.EntityId;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.gql.models.ServiceModelGql;

@Component
@RequiredArgsConstructor
class TimeentriesService implements TimeentriesServicePortIn {

    private final ActionsGrpcPortOut timeentriesGateway;
    
    @Override
    public EntityId newEntry(String requestorEmail, UUID projectId, LocalDate now) {
        return timeentriesGateway.newAction(requestorEmail, projectId, now);
    }

    @Override
    public boolean remove(UUID projectId, UUID entityId, int entityVersion) {
        return timeentriesGateway.remove(projectId, entityId, entityVersion);
    }

    @Override
    public boolean update(EntityId entityId, String customerId, String description, int distance, int duration,
            String servicemanEmail, String servicemanName, LocalDate whenProvided) {
        return timeentriesGateway.update(entityId, customerId, description, distance, duration, servicemanEmail, servicemanName, whenProvided);
    }

    @Override
    public ServiceModelGql getAction(UUID projectId, UUID entityId, Function<String, CustomerEntityGql> customerMapper) {
        return timeentriesGateway.getAction(projectId, entityId, customerMapper);
    }

    @Override
    public List<ServiceModelGql> search(UUID projectId, LocalDate from, LocalDate to,
            Function<String, CustomerEntityGql> customerMapper) {
        return timeentriesGateway.search(projectId, from, to, customerMapper);
    }

}
