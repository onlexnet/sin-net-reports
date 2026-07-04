package sinnet.grpc.actions;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.domain.model.ValEmail;
import sinnet.grpc.common.Mapper;
import sinnet.grpc.mapping.RpcCommandHandlerBase;
import sinnet.grpc.timeentries.ReserveCommand;
import sinnet.grpc.timeentries.ReserveResult;
import sinnet.grpc.timeentries.ReserveCommand.OptionalWhenCase;
import sinnet.models.ActionValue;
import sinnet.write.ActionRepositoryEx;

/**
 * TBD.
 */
@Component
@RequiredArgsConstructor
public class TimeEntriesRpcReserve extends RpcCommandHandlerBase<ReserveCommand, ReserveResult> {

  private final ActionRepositoryEx actionService;

  @Override
  protected ReserveResult apply(ReserveCommand cmd) {
    var whenProvided = cmd.getOptionalWhenCase() == OptionalWhenCase.WHEN
        ? MapperDto.fromDto(cmd.getWhen())
        : LocalDate.now();
    var emailOfCurrentUser = cmd.getInvoker().getRequestorEmail();
    var projectId = UUID.fromString(cmd.getInvoker().getProjectId());
    var model = new ActionValue()
        .setWho(ValEmail.of(emailOfCurrentUser))
        .setWhen(whenProvided);
    var id = UUID.randomUUID();
    var entityId = actionService.save(projectId, id, model);
    var result = Mapper.toDto(entityId);
    return ReserveResult.newBuilder()
        .setEntityId(result)
        .build();
  }

}
