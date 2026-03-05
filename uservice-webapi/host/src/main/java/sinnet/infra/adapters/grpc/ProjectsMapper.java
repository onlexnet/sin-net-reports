package sinnet.infra.adapters.grpc;

import java.util.UUID;

import sinnet.domain.models.ProjectId;
import sinnet.grpc.projects.generated.Project;

/** Fixme. */
public interface ProjectsMapper {

  /** Fixme. */
  public static sinnet.domain.models.Project map(Project grpc) {
    return new sinnet.domain.models.Project(
      new ProjectId(UUID.fromString(grpc.getId().getEId()), grpc.getId().getETag()),
      grpc.getModel().getName()
    );
  }

}
