package sinnet;

import java.util.UUID;

import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Input;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Type;

import lombok.Data;

@Type("SomeEntity")
@Input("MyEntity")
@Data
public class Entity {
  private @NonNull @Id UUID projectId;
  private @NonNull @Id UUID entityId;
  private @NonNull @Id int entityVersion;
}
