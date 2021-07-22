package sinnet.bus.query;

import java.util.UUID;

import org.jmolecules.architecture.cqrs.annotation.QueryModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

public interface ListServicemen {

  @AllArgsConstructor
  @Value
  @Builder
  @Jacksonized
  class Ask {
    /** Address used to send the query to it's handler. */
    public static final String ADDRESS = "query.ListServicemen";
    private UUID projectId;
  }

  @Value
  @Builder
  @Jacksonized
  class Response {
    private Model[] items;
  }

  @QueryModel
  @AllArgsConstructor
  @Value
  @Builder
  @Jacksonized
  class Model {
    private String email;
    private String name;
  }

}
