package sinnet.gql.models;

import org.eclipse.microprofile.graphql.Input;
import org.eclipse.microprofile.graphql.NonNull;

import lombok.Data;

@Data
@Input("CustomerSecretInput")
public class CustomerSecretInput {
  private @NonNull String location;
  private String username;
  private String password;
}
