package sinnet.dbo;

import io.smallrye.mutiny.Uni;
import lombok.Value;
import sinnet.model.Email;
import sinnet.model.ProjectIdHolder;

public interface DboCreate {

  Uni<CreateResult> create(ProjectIdHolder requestedId, Email emailOfOwner);

  sealed interface CreateResult {
  }

  @Value
  final class Success implements CreateResult {
    private ProjectIdHolder value;
  }

  @Value
  final class ValidationFailed implements CreateResult {
    private String reason;
  }

  
}
