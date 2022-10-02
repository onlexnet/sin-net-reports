package sinnet.rpc;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import sinnet.dbo.DboFacade;
import sinnet.grpc.projects.GetReply;
import sinnet.grpc.projects.GetRequest;
import sinnet.model.ValProjectId;

@ApplicationScoped
@RequiredArgsConstructor
class ProjectGetImpl implements ProjectGet {

  private final DboFacade dbo;

  @Override
  public Uni<GetReply> get(GetRequest request) {
    var eidAsString = request.getProjectId().getEId();
    var eid = UUID.fromString(eidAsString);
    var vid = ValProjectId.of(eid);
    return dbo.get(vid).map(it -> it.getModel()).map(it -> GetReply.newBuilder().setModel(it).build());
  }

}
