package sinnet.gql.actions;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.grpc.timeentries.UpdateCommand;
import sinnet.grpc.timeentries.UpdateResult;
import sinnet.models.ActionDuration;
import sinnet.models.ActionValue;
import sinnet.models.Distance;
import sinnet.models.Email;
import sinnet.read.CustomerProjection;
import sinnet.vertx.Handlers;
import sinnet.write.ActionRepository;

/** Fixme. */
@Component
@Slf4j
@RequiredArgsConstructor
public class TimeEntriesRpcUpdate implements Mapper {

  private final ActionRepository actionService;
  private final CustomerProjection customerReader;

  public void command(UpdateCommand cmd, StreamObserver<UpdateResult> responseObserver ) {
    var entityId = fromDto(cmd.getModel().getEntityId());
    var projectId = entityId.getProjectId();
    var entry = cmd.getModel();
    var customerId = UUID.fromString(cmd.getModel().getCustomerId()); // TODO change to untainted ID
    customerReader
            .get(projectId, customerId)
            .map(it -> {
                var model = ActionValue.builder()
                    .who(Email.of(entry.getServicemanName()))
                    .when(fromDto(entry.getWhenProvided()))
                    .what(entry.getDescription())
                    .howLong(ActionDuration.of(entry.getDuration()))
                    .howFar(Distance.of(entry.getDistance()));
                if (it.isDefined()) {
                    var customerModel = it.get();
                    model.whom(customerModel.getId().getId());
                }
                return model;
            })
            .flatMap(it -> {
                return actionService
                    .update(entityId, it .build())
                    .map(o -> Boolean.TRUE);
            })
            .onComplete(Handlers.logged(log, responseObserver, it -> {
                return UpdateResult.newBuilder().setSuccess(it).build();
            }));
    }

}
