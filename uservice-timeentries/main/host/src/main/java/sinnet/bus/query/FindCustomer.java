package sinnet.bus.query;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerContact;
import sinnet.models.CustomerValue;

public interface FindCustomer {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class Ask {
    /** Address used to send the query to it's handler. */
    public static final String ADDRESS = "query.FindCustomer";

    private UUID projectId;
    private UUID entityId;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class Reply {
    private UUID entityId;
    private long entityVersion;
    private CustomerValue value;
    private CustomerSecret[] secrets;
    private CustomerSecretEx[] secretsEx;
    private CustomerContact[] contacts;
  }
}
