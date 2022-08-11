package sinnet.access;

import io.vavr.collection.Array;
import lombok.Value;
import sinnet.model.ProjectIdHolder;

/** Who may delete the project?. */
@Value
final class ActionDeletePermissionChecker implements ActionPermissionChecker {

  /** Investigated project. */
  private final ProjectIdHolder projectId;

  @Override
  public ValidationResult onOwnerRole(Array<ProjectIdHolder> projectsId) {
    return projectsId.contains(projectId)
        ? ValidationResult.PERMITTED
        : ValidationResult.IGNORED;
  }

  @Override
  public ValidationResult onOperatorRole(Array<ProjectIdHolder> projectsId) {
    return ValidationResult.IGNORED;
  }

}
