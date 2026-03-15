package sinnet.app.flow.timeentries;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.TimeentriesServicePortIn;
import sinnet.app.ports.out.ActionsGrpcPortOut;
import sinnet.domain.models.EntityId;
import sinnet.domain.models.TimeEntry;

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
    public TimeEntry getAction(UUID projectId, UUID entityId) {
        return timeentriesGateway.getActionInternal(projectId, entityId);
    }

    @Override
    public List<TimeEntry> search(UUID projectId, LocalDate from, LocalDate to) {
        return timeentriesGateway.searchInternal(projectId, from, to);
    }

}
