package sinnet.gql.api;

import javax.inject.Inject;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;

import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import sinnet.gql.models.ActionsMutation;
import sinnet.gql.security.AccessProvider;
import sinnet.grpc.roles.Rbac;

@GraphQLApi
public class RootMutation {

  @GrpcClient("activities")
  Rbac projectsGrpc;

  @Inject
  AccessProvider accessProvider;

  @Name("Customers")
  @Mutation
  public @NonNull Uni<sinnet.gql.api.CustomersMutation> customers(@NonNull @Id String projectId) {
    return accessProvider.with(projectId)
        .map(it -> new CustomersMutation(projectId, it.getUserToken()));
  }

  @Name("Actions")
  @Mutation
  public @NonNull Uni<sinnet.gql.models.ActionsMutation> actions(@NonNull @Id String projectId) {
    return accessProvider.with(projectId)
        .map(it -> new ActionsMutation(projectId, it.getUserToken()));
  }
}
