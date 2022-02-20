package sinnet;

import java.util.UUID;

import javax.inject.Inject;
import javax.json.JsonArray;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.jwt.JsonWebToken;

import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import io.vavr.collection.Iterator;
import sinnet.grpc.projects.ListRequest;
import sinnet.grpc.projects.Projects;

@GraphQLApi
public class RootQuery {

  @Inject
  JsonWebToken jwt;
  
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
          .map(o -> {
            var a = ProjectEntity.builder().id(o.getId()).name(o.getName()).build();
            return a;
          })
          .toJavaArray(ProjectEntity[]::new));
  }

  @Query("Customers")
  public @NonNull CustomersQuery customers(@NonNull @Id @Name("projectId") String projectIdAsString) {
    var projectId = UUID.fromString(projectIdAsString);
    return new CustomersQuery(projectId);
  }

  @Query("Actions")
  public @NonNull ActionsQuery actions(@NonNull @Id @Name("projectId") String projectIdAsString) {
    var projectId = UUID.fromString(projectIdAsString);
    return new ActionsQuery(projectId);
  }

  @Query
  public Users Users(@NonNull @Id @Name("projectId") String projectIdAsString) {
    var projectId = UUID.fromString(projectIdAsString);
    return new Users(projectId);
  }


}
