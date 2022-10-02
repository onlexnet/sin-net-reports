package sinnet.rpc;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import sinnet.access.AccessFacade;
import sinnet.dbo.DboUpdate;
import sinnet.grpc.projects.UpdateCommand;
import sinnet.grpc.projects.UpdateResult;
import sinnet.model.ValProjectId;

@ApplicationScoped
@RequiredArgsConstructor
class ProjectsUpdateImpl implements ProjectsUpdate {
  
  private final AccessFacade accessFacade;
  private final DboUpdate dboUpdate;

  @Override
  public Uni<UpdateResult> update(UpdateCommand request) {
    var eidAsString = request.getEntityId().getEId();
    var eid = UUID.fromString(eidAsString);
    var idHolder = ValProjectId.of(eid);

    var requestor = request.getUserToken();

    return accessFacade.guardAccess(requestor, idHolder, roleContext -> roleContext::canUpdateProject)
      .chain(id -> dboUpdate.update(request));  
  }
}
