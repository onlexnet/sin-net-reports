package sinnet.gql.projects;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.grpc.projects.ListReply;
import sinnet.grpc.projects.ListRequest;
import sinnet.models.Email;
import sinnet.read.ProjectProjector;
import sinnet.vertx.Handlers;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectsRpcList {
  
  private final ProjectProjector.Provider projectProjector;

  public void query(ListRequest request, StreamObserver<ListReply> responseObserver) {
    var email = request.getEmailOfRequestor();
    projectProjector
        .findByServiceman(Email.of(email))
        .map(it -> ListReply.newBuilder()
            .addAllProjects(Mapper.map(it))
            .build())
        .onComplete(Handlers.logged(log, responseObserver, it -> it));
  }
}
