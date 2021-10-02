package sinnet;

import java.util.UUID;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
public class RootQuery {

  @Query
  public @NonNull PrincipalModel getPrincipal() {
    return new PrincipalModel();
  }

  @Query
  public @NonNull ProjectEntity[] availableProjects() {
    return new ProjectEntity[0];
  }

  @Query
  public @NonNull CustomersQuery customers(UUID projectId) {
    return new CustomersQuery(projectId);
  }

}
