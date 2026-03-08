package sinnet.infra.adapters.grpc;

import java.util.UUID;

import sinnet.domain.models.Project;
import sinnet.domain.models.ProjectId;

/** Fixme. */
interface ProjectsMapper {

  /** Fixme. */
  public static Project map(sinnet.grpc.projects.generated.Project project) {
    return new Project(
        new ProjectId(UUID.fromString(project.getId().getEId()), project.getId().getETag()),
        project.getModel().getName());
  }
}
