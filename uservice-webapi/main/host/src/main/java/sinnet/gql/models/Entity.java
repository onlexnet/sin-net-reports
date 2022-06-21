package sinnet.gql.models;

import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Input;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Type("SomeEntity")
@Input("MyEntity")
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class Entity {
  private @NonNull @Id String projectId;
  private @NonNull @Id String entityId;
  private @NonNull long entityVersion;
}
