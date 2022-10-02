package sinnet.dbo;

import io.smallrye.mutiny.Uni;
import sinnet.model.ValProjectId;

public interface DboRemove {

  /**
   * Unconditionally removed project with given ID from database.
   * <br/>
   * Before invocation, be sure that invoker is permitted to remove given project.
   */
  Uni<Void> remove(ValProjectId eid);

}
