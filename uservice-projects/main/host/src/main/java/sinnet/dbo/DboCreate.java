package sinnet.dbo;

import java.util.UUID;

import io.smallrye.mutiny.Uni;
import lombok.Value;
import sinnet.model.ProjectVid;
import sinnet.model.ValEmail;
import sinnet.model.ValProjectId;

/** Keeps methods / models related to new project creation. */
public interface DboCreate {

  default ValProjectId randomId() {
    return ValProjectId.of(UUID.randomUUID());
  }

  Uni<CreateResult> create(CreateContent entry);

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

  @Value
  final class ValidationFailed implements CreateResult {
    private String reason;
  }

  
}
