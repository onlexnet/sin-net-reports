package sinnet;

import org.eclipse.microprofile.graphql.NonNull;

import lombok.Data;

@Data
public class CustomerEntity {
  private @NonNull Entity id;
  private @NonNull CustomerModel data;
  private @NonNull CustomerSecret[] secrets;
  private @NonNull CustomerSecretEx[] secretsEx;
  private @NonNull CustomerContact[] contacts;
}
