package sinnet.rpc;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.access.AccessFacade;
import sinnet.dbo.DboUpdate;
import sinnet.grpc.projects.UpdateCommand;
import sinnet.grpc.projects.UpdateResult;
import sinnet.model.ValProjectId;

@Component
@RequiredArgsConstructor
class ProjectsUpdateImpl implements RpcCommandHandler<UpdateCommand, UpdateResult> {
  
  private final AccessFacade accessFacade;
  private final DboUpdate dboUpdate;

  @Override
  public UpdateResult apply(UpdateCommand cmd) {
    var eidAsString = cmd.getEntityId().getEId();
    var eid = UUID.fromString(eidAsString);
    var idHolder = ValProjectId.of(eid);

    var requestor = cmd.getUserToken();

    accessFacade.guardAccess(requestor, idHolder, roleContext -> roleContext::canUpdateProject);
    return dboUpdate.update(cmd);
  }
}
