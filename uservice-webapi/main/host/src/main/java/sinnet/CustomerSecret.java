package sinnet;

import org.eclipse.microprofile.graphql.NonNull;

import lombok.Data;

@Data
public class CustomerSecret {
  private @NonNull String location;
  private String username;
  private String password;
  private @NonNull String changedWhen;
  private @NonNull String changedWho;
}
