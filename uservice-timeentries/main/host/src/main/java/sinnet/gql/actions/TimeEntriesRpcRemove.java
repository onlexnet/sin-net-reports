package sinnet.gql.actions;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.grpc.RpcCommandHandler;
import sinnet.grpc.timeentries.RemoveCommand;
import sinnet.grpc.timeentries.RemoveResult;
import sinnet.write.ActionRepositoryEx;

@Component
@RequiredArgsConstructor
@Slf4j
public class TimeEntriesRpcRemove implements
    RpcCommandHandler<RemoveCommand, RemoveResult>,
    sinnet.gql.common.Mapper {

  private final ActionRepositoryEx actionService;

  @Override
  public RemoveResult apply(RemoveCommand request) {
    var id = fromDto(request.getEntityId());
    var result = actionService.remove(id);
    return RemoveResult.newBuilder()
        .setResult(result)
        .build();
  }

}
