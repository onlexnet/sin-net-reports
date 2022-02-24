package sinnet.gql.models;

import org.eclipse.microprofile.graphql.NonNull;

import lombok.Data;
import sinnet.CustomerContact;
import sinnet.CustomerModel;
import sinnet.CustomerSecretEx;

@Data
public class CustomerEntity {
  private @NonNull Entity id;
  private @NonNull CustomerModel data;
  private @NonNull CustomerSecret[] secrets;
  private @NonNull CustomerSecretEx[] secretsEx;
  private @NonNull CustomerContact[] contacts;
}
