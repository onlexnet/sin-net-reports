package sinnet.app.ports.in;

import java.util.List;
import java.util.function.Function;

import sinnet.domain.models.ProjectId;
import sinnet.gql.models.ProjectEntityGql;
import sinnet.grpc.projects.generated.Project;

public interface ProjectsPortIn {

  ProjectId create(String requestorEmail);

  ProjectId update(String requestorEmail, ProjectId id, String name, String ownerEmail, List<String> operatorEmail);

  List<ProjectEntityGql> list(String requestorEmail, Function<Project, ProjectEntityGql> mapper);

  StatsResult userStats(String requestorEmail);

  /**
   * Set of stats related to the current user. Scope of statis is defined by needs of getting info about the user.
   */
  record StatsResult(int numberOfProjects) {
  }
}
