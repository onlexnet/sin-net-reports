package sinnet.bus.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import sinnet.models.Email;
import sinnet.models.ProjectId;

/**
 * Add some serviceman to a project so that the serviceman could record its activities in the project.
 * If the serviceman email is already used, only rest of data is updated
 */
public interface IncludeServicemanInProject {

  @Value
  @AllArgsConstructor
  @Builder
  @Jacksonized
  class Command {
    public static final String ADDRESS = "cmd.IncludeServicemanInProject";
    private ProjectId projectId;
    private Email email;
    private String fullName;
  }

}
