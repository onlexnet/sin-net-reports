package sinnet.gql.actions;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.gql.customers.CustomerRepositoryEx;
import sinnet.grpc.RpcCommandHandler;
import sinnet.grpc.timeentries.UpdateCommand;
import sinnet.grpc.timeentries.UpdateResult;
import sinnet.models.ActionDuration;
import sinnet.models.ActionValue;
import sinnet.models.Distance;
import sinnet.models.Email;
import sinnet.models.ShardedId;
import sinnet.write.ActionRepositoryEx;

/** Fixme. */
@Component
@Slf4j
@RequiredArgsConstructor
public class TimeEntriesRpcUpdate implements RpcCommandHandler<UpdateCommand, UpdateResult>, MapperDto {

  private final ActionRepositoryEx actionService;
  private final CustomerRepositoryEx customerRepo;
  
  @Override
  public UpdateResult apply(UpdateCommand cmd) {
    var entityId = fromDto(cmd.getModel().getEntityId());

    var entryDto = cmd.getModel();
    var entry = fromDto(entryDto);

    var result = actionService.update(entry);
    return UpdateResult.newBuilder().setSuccess(true).build();
  }

}
