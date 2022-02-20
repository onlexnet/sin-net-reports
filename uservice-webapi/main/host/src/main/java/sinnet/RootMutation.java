package sinnet;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;

@GraphQLApi
@ApplicationScoped
public class RootMutation {

  @Name("Customers")
  @Mutation
  public @NonNull CustomersMutation customers(@NonNull @Id @Name("projectId") String projectIdAsString) {
    var projectId = UUID.fromString(projectIdAsString);
    return new CustomersMutation(projectId);
  }

  @Name("Actions")
  @Mutation
  public @NonNull ActionsMutation actions(@NonNull @Id String projectId) {
    var typedProjectId = UUID.fromString(projectId);
    return new ActionsMutation(typedProjectId);
  }
}
