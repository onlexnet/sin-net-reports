package sinnet.access;

import io.vavr.collection.Array;
import lombok.Value;
import sinnet.model.ProjectIdHolder;

@Value
final class ActionUpdatePermissionChecker implements ActionPermissionChecker {

  private final ProjectIdHolder projectId;

  @Override
  public ValidationResult onOwnerRole(Array<ProjectIdHolder> projectsId) {
    return projectsId.contains(projectId)
        ? ValidationResult.PERMITTED
        : ValidationResult.IGNORED;
  }

  @Override
  public ValidationResult onOperatorRole(Array<ProjectIdHolder> projectsId) {
    return projectsId.contains(projectId)
        ? ValidationResult.PERMITTED
        : ValidationResult.IGNORED;
  }

}
