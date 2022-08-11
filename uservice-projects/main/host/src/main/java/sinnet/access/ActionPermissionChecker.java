package sinnet.access;

import io.vavr.collection.Array;
import sinnet.model.ProjectIdHolder;

/**
 * Designed to be:
 * - implemented by action's permission checkers
 * - focused on some implicitly provided 'current' projectId
 * - invoked by {@code RoleContext}
 * <br/>
 * As it is invoked using all known user's roles, if any of methods returned true, action can be performed.
 * Values 'false' shou
 */
interface ActionPermissionChecker {

  /** Returns true if the current action can be performed by and Owner. */
  ValidationResult onOwnerRole(Array<ProjectIdHolder> projectsId);

  /** Returns true if the current action can be performed by an Operator. */
  ValidationResult onOperatorRole(Array<ProjectIdHolder> projectsId);

  enum ValidationResult {
    PERMITTED,
    IGNORED,
    VETOED
  }
}

