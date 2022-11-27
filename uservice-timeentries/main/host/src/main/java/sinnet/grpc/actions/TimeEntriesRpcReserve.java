package sinnet.grpc.actions;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.grpc.mapping.RpcCommandHandler;
import sinnet.grpc.timeentries.ReserveCommand;
import sinnet.grpc.timeentries.ReserveResult;
import sinnet.models.ActionValue;
import sinnet.models.ShardedId;
import sinnet.models.ValEmail;
import sinnet.write.ActionRepositoryEx;

@Component
@RequiredArgsConstructor
public class TimeEntriesRpcReserve implements RpcCommandHandler<ReserveCommand, ReserveResult>, MapperDto {

  private final ActionRepositoryEx actionService;

  @Override
  public ReserveResult apply(ReserveCommand cmd) {
    var emailOfCurrentUser = cmd.getInvoker().getRequestorEmail();
    var whenProvided = fromDto(cmd.getWhen());
    var projectId = UUID.fromString(cmd.getInvoker().getProjectId());
    var model = new ActionValue()
        .setWho(ValEmail.of(emailOfCurrentUser))
        .setWhen(whenProvided);
    var entityId = ShardedId.anyNew(projectId);
    actionService.save(entityId, model);
    var result = toDto(entityId);
    return ReserveResult.newBuilder()
        .setEntityId(result)
        .build();
  }

}
