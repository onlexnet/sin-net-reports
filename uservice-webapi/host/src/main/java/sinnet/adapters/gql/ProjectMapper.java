package sinnet.adapters.gql;

import sinnet.domain.models.Project;
import sinnet.gql.models.ProjectEntityGql;
import sinnet.gql.models.SomeEntityGql;

public interface ProjectMapper {

  public static ProjectEntityGql map(Project model) {
    return new ProjectEntityGql(
        new SomeEntityGql()
          .setEntityId(model.projectId().toString())
          .setProjectId(model.projectId().toString())
          .setEntityVersion(model.tag()),
        model.name());
  }

}
