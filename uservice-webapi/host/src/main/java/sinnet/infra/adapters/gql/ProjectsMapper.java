package sinnet.infra.adapters.gql;

import java.util.UUID;

import sinnet.domain.models.ProjectId;
import sinnet.gql.models.ProjectEntityGql;
import sinnet.gql.models.SomeEntityGql;

/** Fixme. */
public interface ProjectsMapper {

  public static ProjectEntityGql map(sinnet.domain.models.Project project) {
    return new ProjectEntityGql(
        new SomeEntityGql()
          .setEntityId(project.id().toString())
          .setProjectId(project.id().toString())
          .setEntityVersion(project.id().tag()),
        project.name());
  }

}
