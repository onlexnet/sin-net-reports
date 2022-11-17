package sinnet.bus.commands;

import sinnet.models.Email;
import sinnet.models.ShardedId;
import sinnet.models.ProjectId;

/**
 * Add some serviceman to a project so that the serviceman could record its
 * activities in the project.
 * If the serviceman email is already used, only rest of data is updated
 */
public interface IncludeServicemanInProject {

  ShardedId onRequest(Command cmd);
  
  record Command(
      ProjectId projectId,
      Email email,
      String fullName) {
  }

}
