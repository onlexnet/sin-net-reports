package sinnet.grpc.actions;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.grpc.common.Mapper;
import sinnet.grpc.mapping.RpcCommandHandlerBase;
import sinnet.grpc.timeentries.RemoveCommand;
import sinnet.grpc.timeentries.RemoveResult;
import sinnet.write.ActionRepositoryEx;

/**
 * TBD.
 */
@Component
@RequiredArgsConstructor
public class TimeEntriesRpcRemove extends RpcCommandHandlerBase<RemoveCommand, RemoveResult> {

  private final ActionRepositoryEx actionService;

  @Override
  public RemoveResult apply(RemoveCommand request) {
    var id = Mapper.fromDto(request.getEntityId());
    var result = actionService.remove(id);
    return RemoveResult.newBuilder()
        .setResult(result)
        .build();
  }

}
