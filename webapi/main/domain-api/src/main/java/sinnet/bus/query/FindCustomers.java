package sinnet.bus.query;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.Builder.Default;
import lombok.extern.jackson.Jacksonized;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerContact;
import sinnet.models.CustomerValue;
import sinnet.models.UserToken;

public interface FindCustomers {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class Ask {
    /** Address used to send the query to it's handler. */
    public static final String ADDRESS = "query.FindCustomers";
    private UUID projectId;
    private UserToken invoker;
  }

  
  @Value
  @Builder
  @Jacksonized
  class Reply {
    private CustomerData[] data;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  class CustomerData {
    private UUID projectId;
    private UUID entityId;
    private int entityVersion;
    private CustomerValue value;
    @Default
    private CustomerSecret[] secrets = new CustomerSecret[0];
    @Default
    private CustomerSecretEx[] secretsEx = new CustomerSecretEx[0];
    @Default
    private CustomerContact[] contacts = new CustomerContact[0];
  }
}
