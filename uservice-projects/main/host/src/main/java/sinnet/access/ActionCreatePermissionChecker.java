package sinnet.access;

import io.vavr.collection.Array;
import lombok.Value;
import sinnet.model.ValProjectId;

/** Checks if is allowed to create new project. */
@Value
final class ActionCreatePermissionChecker implements ActionPermissionChecker {

  private final ValProjectId projectId;

  /** To avoid creation of inifinitive number of projects, lets create some artificial limit. */
  @Override
  public ValidationResult onOwnerRole(Array<ValProjectId> projectsId) {
    return ValidationResult.PERMITTED;
  }

  /** For creation new project beign OPerator os some other projects doesn't matter. */
  @Override
  public ValidationResult onOperatorRole(Array<ValProjectId> projectsId) {
    return ValidationResult.IGNORED;
  }

}
