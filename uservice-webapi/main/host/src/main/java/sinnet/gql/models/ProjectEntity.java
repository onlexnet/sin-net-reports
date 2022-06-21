package sinnet.gql.models;

import org.eclipse.microprofile.graphql.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class ProjectEntity {
  private Entity entity ;
  private @NonNull String name;
}
