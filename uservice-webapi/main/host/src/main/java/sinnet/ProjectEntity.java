package sinnet;

import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class ProjectEntity {
  private @Id @NonNull String id;
  private @NonNull String name;
}
