package sinnet.gql.projects;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import sinnet.grpc.projects.ListReply;
import sinnet.grpc.projects.ListRequest;
import sinnet.grpc.projects.ProjectsGrpc.ProjectsImplBase;

@Component
@RequiredArgsConstructor
public class ProjectsRpc extends ProjectsImplBase {
  
  private final ProjectsRpcList list;

  @Override
  public void list(ListRequest request, StreamObserver<ListReply> responseObserver) {
    list.query(request, responseObserver);
  }
}
