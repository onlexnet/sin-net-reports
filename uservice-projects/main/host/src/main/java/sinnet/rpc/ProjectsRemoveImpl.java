package sinnet.rpc;

import java.util.UUID;

import org.springframework.stereotype.Component;

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
  public RemoveResult apply(RemoveCommand cmd) {
    var eidAsString = cmd.getProjectId().getEId();
    var eid = UUID.fromString(eidAsString);
    var idHolder = ValProjectId.of(eid);

    var requestor = cmd.getUserToken();

    accessFacade.guardAccess(requestor, idHolder, rc -> rc::canDeleteProject);
    dboRemove.remove(idHolder);
    return RemoveResult.newBuilder().setSuccess(true).build();
  }

}
