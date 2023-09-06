package sinnet.gql.api;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.grpc.common.UserToken;
import sinnet.web.AuthenticationToken;

@Controller
@RequiredArgsConstructor
class Query {
  
  @QueryMapping("Projects")
  ProjectsQuery projects() {
    var authentication = (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    var primaryEmail = authentication.getPrincipal();

    return ProjectsQuery.of(primaryEmail);
  }

  @QueryMapping("Users")
  UsersQuery users(@Argument String projectId) {
    var authentication = (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    var primaryEmail = authentication.getPrincipal();

    return new UsersQuery(projectId, primaryEmail);
  }

  @QueryMapping("Actions")
  ActionsQuery actions(@Argument String projectId) {
    var authentication = (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    var primaryEmail = authentication.getPrincipal();

    return new ActionsQuery(projectId, primaryEmail);
  }

  @QueryMapping("Customers")
  CustomersQuery customers(@Argument String projectId) {
    var authentication = (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    var primaryEmail = authentication.getPrincipal();

    var userToken = UserToken.newBuilder()
        .setProjectId(projectId)
        .setRequestorEmail(primaryEmail)
        .build();
    return new CustomersQuery(userToken);
  }
}
