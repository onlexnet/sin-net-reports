package sinnet;

import java.util.UUID;

import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.NonNull;

import lombok.Data;

@Data
public class MyEntity {
  private @NonNull @Id UUID projectId;
  private @NonNull UUID entityId;
  private @NonNull @Id UUID entityVersion;
}
