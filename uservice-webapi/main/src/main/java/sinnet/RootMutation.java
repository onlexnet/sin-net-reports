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
  public CustomersMutation customers(@NonNull @Id UUID projectId) {
    return new CustomersMutation(projectId);
  }

  @Name("Actions")
  @Mutation
  public ActionsMutation actions(@NonNull @Id UUID projectId) {
    return new ActionsMutation(projectId);
  }
}
