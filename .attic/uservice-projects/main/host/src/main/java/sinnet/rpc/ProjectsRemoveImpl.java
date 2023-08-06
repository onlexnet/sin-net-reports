package sinnet.rpc;

import static io.vavr.control.Either.right;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.grpc.Status;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import sinnet.access.AccessFacade;
import sinnet.dbo.DboRemove;
import sinnet.grpc.projects.RemoveCommand;
import sinnet.grpc.projects.RemoveResult;
import sinnet.model.ValProjectId;

@Component
@RequiredArgsConstructor
final class ProjectsRemoveImpl implements RpcCommandHandler<RemoveCommand, RemoveResult> {

  private final AccessFacade accessFacade;
  private final DboRemove dboRemove;
  
  @Override
  public Either<Status, RemoveResult> apply(RemoveCommand cmd) {
    var eidAsString = cmd.getProjectId().getEId();
    var eid = UUID.fromString(eidAsString);
    var idHolder = ValProjectId.of(eid);

    var requestor = cmd.getUserToken();

    accessFacade.guardAccess(requestor, idHolder, rc -> rc::canDeleteProject);
    dboRemove.remove(idHolder);
    var result = RemoveResult.newBuilder().setSuccess(true).build();
    return right(result);
  }

}
