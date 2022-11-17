package sinnet.bus.query;

import java.util.UUID;

import org.jmolecules.architecture.cqrs.annotation.QueryModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

public interface ListServicemen {

  @Data
  class Request {
    private UUID projectId;
  }

  @Data
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
