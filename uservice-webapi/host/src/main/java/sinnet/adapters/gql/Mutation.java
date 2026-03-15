package sinnet.adapters.gql;

import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.web.AuthenticationToken;

@Controller
@RequiredArgsConstructor
class Mutation {
  
  @MutationMapping("Projects")
  ProjectsMutation projects() {
    var authentication = (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    var primaryEmail = authentication.getPrincipal();

    return new ProjectsMutation(primaryEmail);
  }

  @MutationMapping("Customers")
  CustomersMutation customers(@Argument UUID projectId) {
    var authentication = (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    var primaryEmail = authentication.getPrincipal();

    var userToken = new sinnet.domain.models.UserToken(projectId, primaryEmail);
    return new CustomersMutation(projectId, userToken);
  }

  @MutationMapping("Actions")
  ActionsMutation actions(@Argument UUID projectId) {
    var authentication = (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    var primaryEmail = authentication.getPrincipal();

    var userToken = new sinnet.domain.models.UserToken(projectId, primaryEmail);
    return new ActionsMutation(projectId, userToken);
  }
}
