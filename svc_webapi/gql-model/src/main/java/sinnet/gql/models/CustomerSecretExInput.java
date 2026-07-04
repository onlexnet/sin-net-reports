package sinnet.gql.models;

import lombok.Data;
import lombok.experimental.Accessors;

/** Fixme. */
@Data
@Accessors(chain = true)
public class CustomerSecretExInput {
  private String location;
  private String username;
  private String password;
  private String entityName;
  private String entityCode;
  private String otpSecret;
  private String otpRecoveryKeys;
}
