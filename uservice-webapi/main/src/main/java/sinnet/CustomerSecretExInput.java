package sinnet;

import org.eclipse.microprofile.graphql.NonNull;

import lombok.Data;

@Data
public class CustomerSecretExInput {
  private @NonNull String location;
  private String username;
  private String password;
  private String entityName;
  private String entityCode;
}
