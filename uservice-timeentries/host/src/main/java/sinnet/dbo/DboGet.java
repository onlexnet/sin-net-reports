package sinnet.dbo;

import java.util.List;

import io.vavr.collection.Iterator;
import sinnet.domain.model.ValEmail;
import sinnet.domain.model.ValProjectId;
import sinnet.grpc.projects.generated.Project;

/**
 * IDs of all projects owned by a given user.
 *
 * @param ownerEmail email ow the give user
 */
interface DboGet {

  /** List of all projects where given email is marked as owner. */
  List<Project> ownedAsProject(ValEmail ownerEmail);
  
  /** List of all projects where given email is marked as owner. */
  List<ValProjectId> ownedAsId(ValEmail ownerEmail);

  /** All project where the the operator is assigned as serviceman. */
  List<ValProjectId> assignedAsId(ValEmail operatorEmail);
  
  StatsResult getStats(ValEmail ownerEmail);

  Project get(ValProjectId projectId);

  List<Project> getAll(Iterator<ValProjectId> projectId);

  boolean isOwner(ValEmail email, ValProjectId projectId);

  record StatsResult(int numberOdProjects) {
  }
}
