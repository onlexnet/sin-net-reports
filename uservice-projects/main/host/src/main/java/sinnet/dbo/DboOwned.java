package sinnet.dbo;

import io.smallrye.mutiny.Uni;
import io.vavr.collection.Array;
import sinnet.grpc.projects.Project;
import sinnet.model.Email;
import sinnet.model.ProjectIdHolder;

/**
 * IDs of all projects owned by a given user.
 *
 * @param ownerEmail email ow the give user
 */
interface DboOwned {

  Uni<Array<Project>> ownedAsProject(Email ownerEmail);
  
  Uni<Array<ProjectIdHolder>> ownedAsId(Email ownerEmail);
}
