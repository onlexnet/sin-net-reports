package sinnet.grpc.customers;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.grpc.mapping.RpcCommandHandlerBase;
import sinnet.models.ShardedId;

/**
 * TBD.
 */
@Component
@RequiredArgsConstructor
public class CustomersRpcRemove extends RpcCommandHandlerBase<RemoveRequest, RemoveReply> {

  private final CustomerRepositoryEx repository;

  @Override
  protected RemoveReply apply(RemoveRequest request) {
    var id = request.getEntityId();
    var projectIdAsUuid = UUID.fromString(id.getProjectId());
    var entityIdAsUuid = UUID.fromString(id.getEntityId());
    var entityId = ShardedId.of(projectIdAsUuid, entityIdAsUuid, id.getEntityVersion());
    var result = repository.remove(entityId);
    return RemoveReply.newBuilder()
        .setSuccess(result)
        .build();
  }


}
