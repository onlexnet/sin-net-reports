package sinnet;

import java.util.UUID;

import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.NonNull;

import lombok.Data;

@Data
public class User {
  private @NonNull @Id UUID entityId;
  private @NonNull String email;
}
