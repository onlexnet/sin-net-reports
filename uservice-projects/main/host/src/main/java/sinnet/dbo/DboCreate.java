package sinnet.dbo;

import java.util.UUID;

import lombok.Value;
import sinnet.model.ProjectVid;
import sinnet.model.ValEmail;
import sinnet.model.ValProjectId;

/** Keeps methods / models related to new project creation. */
public interface DboCreate {

  default ValProjectId randomId() {
    return ValProjectId.of(UUID.randomUUID());
  }

  CreateResult create(CreateContent entry);

  @Value
  class CreateContent {
    private ValProjectId requestedId;
    private ValEmail emailOfOwner;
  }

  
  sealed interface CreateResult {
  }

  @Value
  final class Success implements CreateResult {
    private ProjectVid vid;
  }

  /** Content is not valid (e.g. too long name) */
  @Value
  final class InvalidContent implements CreateResult {
    private String reason;
  }

  /** Requested data is above allowed usage of resources (e.g. too many projects). */
  @Value
  final class AboveLimits implements CreateResult {
    private String reason;
  }

  
}
