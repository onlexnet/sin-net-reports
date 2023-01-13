package sinnet.access;

import java.util.function.Function;
import java.util.function.Predicate;

import sinnet.grpc.projects.UserToken;
import sinnet.model.ValProjectId;

public interface AccessFacade {

  /**
   * Checks if given user is entitled to make an action on given project.
   * @throws StatusException if user is not entitled to make an action.
   */
  void guardAccess(UserToken requestor, ValProjectId eid, Function<RoleContext, Predicate<ValProjectId>> entitlementSelector);
}
