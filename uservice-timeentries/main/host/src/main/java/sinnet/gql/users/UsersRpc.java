package sinnet.gql.users;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import sinnet.grpc.users.SearchReply;
import sinnet.grpc.users.SearchRequest;
import sinnet.grpc.users.UsersGrpc.UsersImplBase;


@Component
@RequiredArgsConstructor
public class UsersRpc extends UsersImplBase {

  private final UsersRpcSearch search;

  @Override
  public void search(SearchRequest request, StreamObserver<SearchReply> responseObserver) {
    search.query(request, responseObserver);
  }
}
