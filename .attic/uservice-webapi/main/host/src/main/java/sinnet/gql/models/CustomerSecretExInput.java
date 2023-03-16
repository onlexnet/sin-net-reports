package sinnet.gql.models;

import org.eclipse.microprofile.graphql.Input;
import org.eclipse.microprofile.graphql.NonNull;

import lombok.Data;

@Data
@Input("CustomerSecretExInput")
public class CustomerSecretExInput {
  private @NonNull String location;
  private String username;
  private String password;
  private String entityName;
  private String entityCode;
}
