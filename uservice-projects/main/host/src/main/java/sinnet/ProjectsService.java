package sinnet;

import javax.inject.Inject;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import sinnet.grpc.projects.Projects;
import sinnet.grpc.projects.RemoveReply;
import sinnet.grpc.projects.RemoveRequest;

@GrpcService
@RequiredArgsConstructor
public class ProjectsService implements Projects {

  @Inject
  @Delegate(types=ProjectsReserve.class)
  private final ProjectsReserve projectsReserve;

  @Inject
  @Delegate(types=ProjectsUpdate.class)
  private final ProjectsUpdate projectsUpdate;

  @Inject
  @Delegate(types=ProjectList.class)
  private final ProjectList projectsList;

  @Override
  public Uni<RemoveReply> remove(RemoveRequest request) {
    // TODO Auto-generated method stub
    return null;
  }

}
