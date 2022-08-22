package sinnet.rpc;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import sinnet.access.AccessFacade;
import sinnet.dbo.DboRemove;
import sinnet.grpc.projects.RemoveCommand;
import sinnet.grpc.projects.RemoveResult;
import sinnet.model.ValProjectId;

@ApplicationScoped
@RequiredArgsConstructor
final class ProjectsRemoveImpl implements ProjectsRemove {

  private final AccessFacade accessFacade;
  private final DboRemove dboRemove;
  
  @Override
  public Uni<RemoveResult> remove(RemoveCommand request) {
    var eidAsString = request.getProjectId().getEId();
    var eid = UUID.fromString(eidAsString);
    var idHolder = ValProjectId.of(eid);

    var requestor = request.getUserToken();

    return accessFacade.guardAccess(requestor, idHolder, rc -> rc::canDeleteProject)
      .chain(dboRemove::remove)
      .map(it -> RemoveResult.newBuilder().setSuccess(true).build());
  }

}
