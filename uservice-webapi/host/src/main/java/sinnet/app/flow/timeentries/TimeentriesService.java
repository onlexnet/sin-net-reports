package sinnet.app.flow.timeentries;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.TimeentriesServicePortIn;
import sinnet.domain.models.EntityId;
import sinnet.infra.adapters.grpc.ActionsGrpcFacade;

@Component
@RequiredArgsConstructor
class TimeentriesService implements TimeentriesServicePortIn {

    private final ActionsGrpcFacade actionsGrpc;
    
    @Override
    public EntityId newEntry(String requestorEmail, UUID projectId, LocalDate now) {
        return actionsGrpc.newAction(requestorEmail, projectId, now);
    }
}
