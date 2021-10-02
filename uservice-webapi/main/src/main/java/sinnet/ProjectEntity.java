package sinnet;

import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.NonNull;

import lombok.Data;

@Data
class ProjectEntity {
  private @Id @NonNull String id;
  private @NonNull String name;
}
