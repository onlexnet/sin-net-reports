package sinnet.infra.adapters.grpc;

import java.util.UUID;

import sinnet.grpc.projects.generated.Project;

/** Fixme. */
public interface ProjectMapper {

  /** Fixme. */
  public static sinnet.domain.models.Project map(Project grpc) {
    return new sinnet.domain.models.Project(
        UUID.fromString(grpc.getId().getEId()),
        grpc.getId().getETag(),
        grpc.getModel().getName());
  }

}
