package sinnet.domain.access;

import java.util.List;

import lombok.Value;
import sinnet.domain.model.ValProjectId;


/** Who may delete the project?. */
@Value
final class ActionDeletePermissionChecker implements ActionPermissionChecker {

  /** Investigated project. */
  private final ValProjectId projectId;

  @Override
  public ValidationResult onOwnerRole(List<ValProjectId> projectsId) {
    return projectsId.contains(projectId)
        ? ValidationResult.PERMITTED
        : ValidationResult.IGNORED;
  }

  @Override
  public ValidationResult onOperatorRole(List<ValProjectId> projectsId) {
    return ValidationResult.IGNORED;
  }

}
