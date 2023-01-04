package sinnet.dbo;

import sinnet.model.ValProjectId;

public interface DboRemove {

  /**
   * Unconditionally removed project with given ID from database.
   * <br/>
   * Before invocation, be sure that invoker is permitted to remove given project.
   */
  void remove(ValProjectId eid);

}
