package sinnet.grpc.actions;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.grpc.customers.CustomerRepositoryEx;
import sinnet.grpc.mapping.RpcCommandHandlerBase;
import sinnet.grpc.timeentries.UpdateCommand;
import sinnet.grpc.timeentries.UpdateResult;
import sinnet.write.ActionRepositoryEx;

/**
 * TBD.
 */
@Component
@RequiredArgsConstructor
public class TimeEntriesRpcUpdate extends RpcCommandHandlerBase<UpdateCommand, UpdateResult> implements MapperDto {

  private final ActionRepositoryEx actionService;
  private final CustomerRepositoryEx customerRepo;
  
  @Override
  protected UpdateResult apply(UpdateCommand cmd) {
    var entityId = fromDto(cmd.getModel().getEntityId());

    var entryDto = cmd.getModel();
    var entry = fromDto(entryDto);

    var result = actionService.update(entry);
    return UpdateResult.newBuilder().setSuccess(true).build();
  }

}
