package sinnet.access;

import io.vavr.collection.Array;
import lombok.Value;
import lombok.experimental.Accessors;
import sinnet.access.ActionPermissionChecker.ValidationResult;
import sinnet.model.ValProjectId;

/** 
 * Marker interface for all known user roles set.
 *
 *<p>The set is calculated for all resources permitted for some UserToken.
 */
public sealed interface RoleContextSet {

  /** Invokes proper method based on user's role, and returns the result of validation. */
  ValidationResult processPermission(ActionPermissionChecker validator);

  /** List of project where the user has access as the Owner. */
  @Value
  @Accessors(fluent = true)
  class OwnerRoleContext implements RoleContextSet {
    private final Array<ValProjectId> projectsIds;

    @Override
    public ValidationResult processPermission(ActionPermissionChecker validator) {
      return validator.onOwnerRole(projectsIds);
    }
  }
  
  /** List of project where the user has access as Operator. */
  @Value
  @Accessors(fluent = true)
  class OperatorRoleContext implements RoleContextSet {
    private final Array<ValProjectId> projectsId;

    @Override
    public ValidationResult processPermission(ActionPermissionChecker validator) {
      return validator.onOperatorRole(projectsId);
    }
  }

}
