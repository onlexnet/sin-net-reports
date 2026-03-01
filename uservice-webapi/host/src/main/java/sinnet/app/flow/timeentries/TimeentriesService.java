package sinnet.app.flow.timeentries;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.TimeentriesServicePortIn;
import sinnet.app.ports.out.ActionsGrpcPortOut;
import sinnet.domain.models.EntityId;

@Component
@RequiredArgsConstructor
class TimeentriesService implements TimeentriesServicePortIn {

    private final ActionsGrpcPortOut actionsGrpc;
    
    @Override
    public EntityId newEntry(String requestorEmail, UUID projectId, LocalDate now) {
        return actionsGrpc.newAction(requestorEmail, projectId, now);
    }

    @Override
    public boolean remove(UUID projectId, UUID entityId, int entityVersion) {
        return actionsGrpc.remove(projectId, entityId, entityVersion);
    }
}
