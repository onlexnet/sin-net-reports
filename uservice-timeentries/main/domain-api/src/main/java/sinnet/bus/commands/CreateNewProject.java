package sinnet.bus.commands;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

public interface CreateNewProject {
  
  @Value
  @Builder
  @Jacksonized
  class Command {
    public static final String ADDRESS = "cmd.CreateNewProject";
    private String name;
  }
}
