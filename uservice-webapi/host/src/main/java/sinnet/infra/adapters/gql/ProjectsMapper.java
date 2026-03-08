package sinnet.infra.adapters.gql;

import sinnet.domain.models.Project;
import sinnet.gql.models.ProjectEntityGql;
import sinnet.gql.models.SomeEntityGql;

/** Fixme. */
public interface ProjectsMapper {

  /** Fixme. */
  public static ProjectEntityGql map(Project model) {
    return new ProjectEntityGql(
        new SomeEntityGql()
            .setEntityId(model.id().id().toString())
            .setProjectId(model.id().id().toString())
            .setEntityVersion(model.id().tag()),
        model.name());
  }

}
