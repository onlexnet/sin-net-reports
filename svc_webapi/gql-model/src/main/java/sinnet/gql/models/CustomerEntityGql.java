package sinnet.gql.models;

import lombok.Data;
import lombok.experimental.Accessors;

/** Fixme. */
@Data
@Accessors(chain = true)
public class CustomerEntityGql {
  private SomeEntityGql id;
  private CustomerModelGql data;
  private CustomerSecretGql[] secrets;
  private CustomerSecretExGql[] secretsEx;
  private CustomerContactGql[] contacts;
}
