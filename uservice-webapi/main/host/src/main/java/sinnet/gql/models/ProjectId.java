package sinnet.gql.models;

import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Input;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Type("ProjectIdModel")
@Input("ProjectIdInput")
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class ProjectId {
  private @NonNull @Id String id;
  private @NonNull long tag;
}
