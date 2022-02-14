package sinnet.gql.actions;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.grpc.timeentries.ReserveCommand;
import sinnet.grpc.timeentries.ReserveResult;
import sinnet.models.ActionValue;
import sinnet.models.Email;
import sinnet.models.EntityId;
import sinnet.vertx.Handlers;
import sinnet.write.ActionRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class TimeEntriesRpcReserve implements Mapper {

    private final ActionRepository actionService;

    public void command(ReserveCommand cmd, StreamObserver<ReserveResult> observer) {
        var emailOfCurrentUser = cmd.getInvoker().getRequestorEmail();
        var whenProvided = fromDto(cmd.getWhen());
        var projectId = UUID.fromString(cmd.getInvoker().getProjectId());
        var model = ActionValue.builder()
            .who(Email.of(emailOfCurrentUser))
            .when(whenProvided)
            .build();
        var entityId = EntityId.anyNew(projectId);

        actionService.save(entityId, model)
            .onComplete(Handlers.logged(log, observer, it -> {
                var result = toDto(entityId);
                return ReserveResult.newBuilder()
                    .setEntityId(result)
                    .build();
            }));
    }
}

