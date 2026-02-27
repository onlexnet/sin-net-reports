package sinnet.infra.adapters.grpc;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.out.UsersServicePortOut;
import sinnet.grpc.users.SearchReply;
import sinnet.grpc.users.SearchRequest;
import sinnet.grpc.users.UsersGrpc.UsersBlockingStub;

@Component
@RequiredArgsConstructor
class UsersGrpcService implements UsersServicePortOut {

  private final UsersBlockingStub stub;

  @Override
  public SearchReply search(SearchRequest request) {
    return stub.search(request);
  }

}
