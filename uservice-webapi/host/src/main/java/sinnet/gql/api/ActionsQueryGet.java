package sinnet.gql.api;

import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import io.vavr.Function1;
import lombok.RequiredArgsConstructor;
import sinnet.gql.models.ServiceModelGql;
import sinnet.ports.timeentries.ActionsGrpcFacade;
import sinnet.ports.timeentries.CustomersGrpcFacade;

@Controller
@RequiredArgsConstructor
class ActionsQueryGet {

  private final ActionsGrpcFacade service;
  private final CustomersGrpcFacade customerService;
  private final CustomerMapper customerMapper;
  private final CommonMapper commonMapper;

  @SchemaMapping
  ServiceModelGql get(ActionsQuery self, @Argument String actionId) {
    var projectId = UUID.fromString(self.projectId());
    var actionIdTyped = UUID.fromString(actionId);

    var customerGet = Function1.of((String customerId) -> 
        customerService.customerGet(self.projectId(), self.primaryEmail(), customerId, customerMapper::toGql));
    var result = service.getAction(projectId, actionIdTyped, customerGet, commonMapper);

    return result;
  }
}

