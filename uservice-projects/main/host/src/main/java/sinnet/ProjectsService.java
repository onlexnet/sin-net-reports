package sinnet;

import io.quarkus.grpc.GrpcService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import sinnet.grpc.projects.Projects;

@GrpcService
@RequiredArgsConstructor
public class ProjectsService implements Projects {

  @Delegate(types = ProjectsReserve.class)
  private final ProjectsReserve projectsReserve;

  @Delegate(types = ProjectsUpdate.class)
  private final ProjectsUpdate projectsUpdate;

  @Delegate(types = ProjectList.class)
  private final ProjectList projectsList;

  @Delegate(types = ProjectsRemove.class)
  private final ProjectsRemove projectsRemove;

  @Delegate(types = ProjectUserStats.class)
  private final ProjectUserStats projectUserStats;

}
