package sinnet.gql.models;

import lombok.Data;

/** Fixme. */
@Data
public class CustomerEntityGql {
  private SomeEntityGql id;
  private CustomerModelGql data;
  private CustomerSecretGql[] secrets;
  private CustomerSecretExGql[] secretsEx;
  private CustomerContactGql[] contacts;
}
