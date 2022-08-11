package sinnet.rpc;

import io.quarkus.grpc.GrpcService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import sinnet.grpc.projects.Projects;

@GrpcService
@RequiredArgsConstructor
class ProjectsService implements Projects {

  @Delegate
  private final ProjectsCreate projectsCreate;

  @Delegate(types = ProjectsUpdate.class)
  private final ProjectsUpdate projectsUpdate;

  @Delegate(types = ProjectList.class)
  private final ProjectList projectsList;

  @Delegate(types = ProjectsRemove.class)
  private final ProjectsRemove projectsRemove;

  @Delegate(types = ProjectUserStats.class)
  private final ProjectUserStats projectUserStats;

}
