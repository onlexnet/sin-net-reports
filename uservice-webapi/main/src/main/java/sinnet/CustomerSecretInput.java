package sinnet;

import org.eclipse.microprofile.graphql.NonNull;

import lombok.Data;

@Data
public class CustomerSecretInput {
  private @NonNull String location;
  private String username;
  private String password;
}
