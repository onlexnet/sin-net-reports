package sinnet;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;

import lombok.Data;

@GraphQLApi
@ApplicationScoped
public class ProjectResource {

  @Query
  public @NonNull ProjectEntity[] availableProjects() {
    return new ProjectEntity[0];
  }
}

@Data
class ProjectEntity {
  private @Id @NonNull String id;
  private @NonNull String name;
}



