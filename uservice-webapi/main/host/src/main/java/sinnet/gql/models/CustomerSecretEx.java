package sinnet.gql.models;

import org.eclipse.microprofile.graphql.NonNull;

import lombok.Data;

@Data
public class CustomerSecretEx {
  private @NonNull String location;
  private String username;
  private String password;
  private String entityName;
  private String entityCode;
  private @NonNull String changedWhen;
  private @NonNull String changedWho;
}
