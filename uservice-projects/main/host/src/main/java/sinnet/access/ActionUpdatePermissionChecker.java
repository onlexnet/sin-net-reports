package sinnet.access;

import io.vavr.collection.Array;
import lombok.Value;
import sinnet.model.ValProjectId;

@Value
final class ActionUpdatePermissionChecker implements ActionPermissionChecker {

  private final ValProjectId projectId;

  @Override
  public ValidationResult onOwnerRole(Array<ValProjectId> projectsId) {
    return projectsId.contains(projectId)
        ? ValidationResult.PERMITTED
        : ValidationResult.IGNORED;
  }

  @Override
  public ValidationResult onOperatorRole(Array<ValProjectId> projectsId) {
    return projectsId.contains(projectId)
        ? ValidationResult.PERMITTED
        : ValidationResult.IGNORED;
  }

}
