package sinnet.bus.query;

import java.util.UUID;

import org.jmolecules.architecture.cqrs.annotation.Command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

public interface CheckProjectPermission {

  @Command
  @AllArgsConstructor
  @Value
  @Jacksonized
  @Builder(toBuilder = true)
  class Ask {
    /** Address used to send the query to it's handler. */
    public static final String ADDRESS = "query.CheckProjectPermission";

    private UUID projectId;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class Reply {
    private Boolean result;
  }

}
