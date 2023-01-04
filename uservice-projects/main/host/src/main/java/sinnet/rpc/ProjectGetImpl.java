package sinnet.rpc;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.dbo.DboFacade;
import sinnet.grpc.projects.GetReply;
import sinnet.grpc.projects.GetRequest;
import sinnet.model.ValProjectId;

@Component
@RequiredArgsConstructor
class ProjectGetImpl implements RpcQueryHandler<GetRequest, GetReply> {

  private final DboFacade dbo;

  @Override
  public GetReply apply(GetRequest request) {
    var eidAsString = request.getProjectId().getEId();
    var eid = UUID.fromString(eidAsString);
    var vid = ValProjectId.of(eid);
    var result = dbo.get(vid).getModel();
    return GetReply.newBuilder().setModel(result).build();
  }

}
