package sinnet;

import java.util.UUID;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;

import lombok.extern.slf4j.Slf4j;

@GraphQLApi
@Slf4j
public class RootQuery {

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
  public @NonNull ProjectEntity[] availableProjects() {
    return new ProjectEntity[0];
  }

  @Query("Customers")
  public @NonNull CustomersQuery customers(@NonNull @Id UUID projectId) {
    return new CustomersQuery(projectId);
  }

  @Query("Actions")
  public @NonNull ActionsQuery actions(@NonNull UUID projectId) {
    log.info("Test");
    return new ActionsQuery(projectId);
  }

  @Query
  public Users Users(@NonNull @Id UUID projectId) {
    return new Users(projectId);
  }


}
