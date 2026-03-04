package sinnet.gql.mappers;

import sinnet.gql.models.ProjectEntityGql;
import sinnet.gql.models.SomeEntityGql;
import sinnet.grpc.projects.generated.Project;

/** Fixme. */
public interface ProjectsMapper {

  /** Fixme. */
  public static ProjectEntityGql toDto(Project grpc) {
    return new ProjectEntityGql(
        new SomeEntityGql()
          .setEntityId(grpc.getId().getEId())
          .setProjectId(grpc.getId().getEId())
          .setEntityVersion(grpc.getId().getETag()),
        grpc.getModel().getName());
  }

}
