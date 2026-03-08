package sinnet.app.ports.out;

import java.util.List;

import sinnet.domain.models.Project;
import sinnet.domain.models.ProjectId;

/** Mockable equivalent of {@link ProjectsGrpcStub}. */
public interface ProjectsPortOut {

  ProjectId create(String requestorEmail);

  ProjectId update(String requestorEmail, ProjectId id, String name, String ownerEmail, List<String> operatorEmail);

  List<Project> list(String requestorEmail);

  StatsResult userStats(String requestorEmail);

  /**
   * Set of stats related to the current user. Scope of statis is defined by needs of getting info about the user.
   */
  record StatsResult(int numberOfProjects) {
  }
}
