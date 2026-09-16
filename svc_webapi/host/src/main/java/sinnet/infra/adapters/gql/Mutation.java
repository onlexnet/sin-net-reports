package sinnet.infra.adapters.gql;

import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.web.AuthenticatedPrincipalResolver;

@Controller
@RequiredArgsConstructor
class Mutation {
  private final AuthenticatedPrincipalResolver principalResolver;
  
  @MutationMapping("Projects")
  ProjectsMutation projects() {
    var primaryEmail = principalResolver.currentPrincipal().email();

    return new ProjectsMutation(primaryEmail);
  }

  @MutationMapping("Customers")
  CustomersMutation customers(@Argument UUID projectId) {
    var primaryEmail = principalResolver.currentPrincipal().email();

    var userToken = new sinnet.domain.models.UserToken(projectId, primaryEmail);
    return new CustomersMutation(projectId, userToken);
  }

  @MutationMapping("Actions")
  ActionsMutation actions(@Argument UUID projectId) {
    var primaryEmail = principalResolver.currentPrincipal().email();

    var userToken = new sinnet.domain.models.UserToken(projectId, primaryEmail);
    return new ActionsMutation(projectId, userToken);
  }
}
