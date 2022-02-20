package sinnet;

import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Input;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Type;

import lombok.Data;

@Type("SomeEntity")
@Input("MyEntity")
@Data
public class Entity {
  private @NonNull @Id String projectId;
  private @NonNull @Id String entityId;
  private @NonNull int entityVersion;
}
