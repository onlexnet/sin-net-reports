package sinnet.gql.api;

import java.util.UUID;

import javax.inject.Inject;
import javax.json.JsonArray;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.jwt.JsonWebToken;

import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import io.vavr.collection.Iterator;
import sinnet.gql.models.ActionsQuery;
import sinnet.gql.models.CustomersQuery;
import sinnet.gql.models.PrincipalModel;
import sinnet.gql.models.ProjectEntity;
import sinnet.gql.security.AccessProvider;
import sinnet.grpc.projects.ListRequest;
import sinnet.grpc.projects.Projects;

@GraphQLApi
public class RootQuery {

  @Inject
  JsonWebToken jwt;
  
  @Inject
  AccessProvider accessProvider;

  @GrpcClient("activities")
  Projects projectsGrpc;

  @Query("getPrincipal")
  public @NonNull PrincipalModel getPrincipal() {
    return new PrincipalModel();
  }

  @Context SecurityContext ctx;
  
  @Query("test")
  public @NonNull PrincipalModel test() {
    var debugName = ctx.getAuthenticationScheme();
    var result = new PrincipalModel();
    result.setName(debugName);
    return result;
  }

  @Query
  public @NonNull Uni<@NonNull ProjectEntity[]> availableProjects() {
    var emails = (JsonArray) jwt.claim("emails").get();
    var email = emails.getString(0);
    var request = ListRequest.newBuilder()
      .setEmailOfRequestor(email)
      .build();
    return projectsGrpc.list(request)
      .map(it -> Iterator
          .ofAll(it.getProjectsList())
          .map(o -> ProjectEntity.builder().id(o.getId()).name(o.getName()).build())
          .toJavaArray(ProjectEntity[]::new));
  }

  @Query("Customers")
  public @NonNull Uni<sinnet.gql.models.CustomersQuery> customers(@NonNull @Id String projectId) {
    return accessProvider.with(projectId)
        .map(it -> new CustomersQuery(projectId, it.getUserToken()));
  }

  @Query("Actions")
  public @NonNull Uni<ActionsQuery> actions(@NonNull @Id  String projectId) {
    return accessProvider.with(projectId)
        .map(it -> new ActionsQuery(projectId, it.getUserToken()));
  }

  @Query("Users")
  public @NonNull Uni<sinnet.gql.api.UsersQuery> users(@NonNull @Id String projectId) {
    return accessProvider.with(projectId)
        .map(it -> new UsersQuery(projectId, it.getUserToken()));
  }


}
