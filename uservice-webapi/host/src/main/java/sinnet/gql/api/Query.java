package sinnet.gql.api;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
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
}
