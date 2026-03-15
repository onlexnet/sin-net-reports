package sinnet.infra.adapters.grpc;

import java.util.UUID;

import org.springframework.stereotype.Component;

import sinnet.app.flow.request.UsersSearchResult;
import sinnet.app.ports.out.UsersServicePortOut;
import sinnet.domain.models.Email;
import sinnet.grpc.common.UserToken;
import sinnet.grpc.users.SearchRequest;
import sinnet.grpc.users.UsersGrpc.UsersBlockingStub;

@Component
class UsersGrpcService implements UsersServicePortOut {

  private final UsersBlockingStub stub;

  public UsersGrpcService(UsersBlockingStub stub) {
    this.stub = stub;
  }

  @Override
  public UsersSearchResult search(UUID projectId, Email requestor) {

    var request = SearchRequest.newBuilder()
        .setUserToken(UserToken.newBuilder().setProjectId(projectId.toString()).setRequestorEmail("ignored@owner").build())
        .build();

    var reply = stub.search(request);
    return new UsersSearchResult(reply.getItemsList().stream()
      .map(it -> new UsersSearchResult.Item(it.getEmail(), it.getEntityId(), it.getCustomName()))
        .toList());
  }

}
