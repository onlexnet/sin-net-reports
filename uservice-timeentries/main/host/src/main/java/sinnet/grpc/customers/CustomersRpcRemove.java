package sinnet.grpc.customers;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.grpc.mapping.RpcCommandHandler;
import sinnet.models.ShardedId;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomersRpcRemove implements
    RpcCommandHandler<RemoveRequest, RemoveReply> {

  private final CustomerRepositoryEx repository;

  @Override
  public RemoveReply apply(RemoveRequest request) {
    var id = request.getEntityId();
    var projectIdAsUUID = UUID.fromString(id.getProjectId());
    var entityIdAsUUID = UUID.fromString(id.getEntityId());
    var entityId = ShardedId.of(projectIdAsUUID, entityIdAsUUID, id.getEntityVersion());
    var result = repository.remove(entityId);
    return RemoveReply.newBuilder()
        .setSuccess(result)
        .build();

  }

}
