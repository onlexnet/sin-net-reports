package sinnet;

import java.util.UUID;

import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.NonNull;

import lombok.Data;

@Data
public class SomeEntity {
  private @NonNull @Id UUID projectId;
  private @NonNull @Id UUID entityId;
  private @NonNull @Id int entityVersion;
}
