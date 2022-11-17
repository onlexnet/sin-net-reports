package sinnet.bus.query;

import java.util.UUID;

import lombok.Data;

public interface CheckProjectPermission {

  class Request {
    private UUID projectId;
  }

  @Data
  class Reply {
    private Boolean result;
  }

}
