package sinnet.infra.adapters.grpc;

import java.util.UUID;

import org.springframework.stereotype.Component;

import sinnet.app.ports.out.UsersServicePortOut;
import sinnet.domain.models.Email;
import sinnet.grpc.common.UserToken;
import sinnet.grpc.users.SearchReply;
import sinnet.grpc.users.SearchRequest;
import sinnet.grpc.users.UsersGrpc.UsersBlockingStub;

@Component
class UsersGrpcService implements UsersServicePortOut {

  private final UsersBlockingStub stub;

  public UsersGrpcService(UsersBlockingStub stub) {
    this.stub = stub;
  }

  @Override
  public SearchReply search(UUID projectId, Email requestor) {

    var request = SearchRequest.newBuilder()
        .setUserToken(UserToken.newBuilder().setProjectId(projectId.toString()).setRequestorEmail("ignored@owner").build())
        .build();

    return stub.search(request);
  }

}
