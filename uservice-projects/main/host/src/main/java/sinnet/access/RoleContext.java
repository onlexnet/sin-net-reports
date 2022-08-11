package sinnet.access;

import java.util.stream.Collectors;

import io.vavr.collection.Array;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Validation;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import sinnet.access.ActionPermissionChecker.ValidationResult;
import sinnet.model.ProjectIdHolder;

/** Holder for all user context sets. When created keeps roles of an User. */
@Value
@RequiredArgsConstructor
public class RoleContext {
  private final Array<RoleContextSet> contextSets;

  public Boolean canCreateProject(ProjectIdHolder projectId) {
    var validator = new ActionCreatePermissionChecker(projectId);
    return collect(validator, contextSets);
  }

  public Boolean canDeleteProject(ProjectIdHolder projectId) {
    var validator = new ActionDeletePermissionChecker(projectId);
    return collect(validator, contextSets);
  }

  public Boolean canUpdateProject(ProjectIdHolder projectId) {
    var validator = new ActionUpdatePermissionChecker(projectId);
    return collect(validator, contextSets);
  }

  private static Boolean collect(ActionPermissionChecker checker, Array<RoleContextSet> contextSets) {
    var setOfValidation = contextSets.map(it -> it.processPermission(checker)).collect(HashSet.collector());
    return toBoolean(setOfValidation);
  }

  private static Boolean toBoolean(Set<ValidationResult> asSet) {
    if (asSet.contains(ValidationResult.VETOED)) {
      return false;
    }
    if (asSet.contains(ValidationResult.PERMITTED)) {
      return true;
    }
    return false;
  }

}

