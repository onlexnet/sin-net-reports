package sinnet.dbo;

import io.smallrye.mutiny.Uni;
import io.vavr.collection.Array;
import sinnet.grpc.projects.Project;
import sinnet.model.ValEmail;
import sinnet.model.ValProjectId;

/**
 * IDs of all projects owned by a given user.
 *
 * @param ownerEmail email ow the give user
 */
interface DboOwned {

  Uni<Array<Project>> ownedAsProject(ValEmail ownerEmail);
  
  Uni<Array<ValProjectId>> ownedAsId(ValEmail ownerEmail);
}
