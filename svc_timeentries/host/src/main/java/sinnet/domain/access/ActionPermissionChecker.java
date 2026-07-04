package sinnet.domain.access;

import java.util.List;

import sinnet.domain.model.ValProjectId;

/**
 * Used to check if the current action can be performed by the user on the set of projects.
 * Designed to be:
 * - implemented by action's permission checkers
 * - focused on some implicitly provided 'current' projectId
 * - invoked by {@code RoleContext}
 * <br/>
 * As it is invoked using all declared user's roles, if any of methods returned true, action can be performed.
 * Values 'false' shou
 */
interface ActionPermissionChecker {

  /** Returns true if the current action can be performed by an Owner. */
  ValidationResult onOwnerRole(List<ValProjectId> projectsId);

  /** Returns true if the current action can be performed by an Operator. */
  ValidationResult onOperatorRole(List<ValProjectId> projectsId);

  enum ValidationResult {
    PERMITTED,
    IGNORED,
    VETOED
  }
}
