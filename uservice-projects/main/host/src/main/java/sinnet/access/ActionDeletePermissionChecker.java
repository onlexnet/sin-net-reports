package sinnet.access;

import io.vavr.collection.Array;
import lombok.Value;
import sinnet.model.ValProjectId;


/** Who may delete the project?. */
@Value
final class ActionDeletePermissionChecker implements ActionPermissionChecker {

  /** Investigated project. */
  private final ValProjectId projectId;

  @Override
  public ValidationResult onOwnerRole(Array<ValProjectId> projectsId) {
    return projectsId.contains(projectId)
        ? ValidationResult.PERMITTED
        : ValidationResult.IGNORED;
  }

  @Override
  public ValidationResult onOperatorRole(Array<ValProjectId> projectsId) {
    return ValidationResult.IGNORED;
  }

}
