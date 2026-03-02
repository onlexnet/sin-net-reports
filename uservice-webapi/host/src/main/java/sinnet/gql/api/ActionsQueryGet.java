package sinnet.gql.api;

import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.app.lib.Functions;
import sinnet.app.ports.in.CustomersPortIn;
import sinnet.app.ports.in.TimeentriesServicePortIn;
import sinnet.app.ports.out.ActionsGrpcPortOut;
import sinnet.app.ports.out.CustomersPortOut;
import sinnet.gql.models.ServiceModelGql;

@Controller
@RequiredArgsConstructor
class ActionsQueryGet {

  private final TimeentriesServicePortIn service;
  private final CustomersPortIn customerService;
  private final CustomerMapper customerMapper;
  private final CommonMapper commonMapper;

  @SchemaMapping
  ServiceModelGql get(ActionsQuery self, @Argument String actionId) {
    var projectId = UUID.fromString(self.projectId());
    var actionIdTyped = UUID.fromString(actionId);

    var customerGet = Functions.of((String customerId) -> 
        customerService.customerGet(self.projectId(), self.primaryEmail(), customerId, customerMapper::toGql));
    var result = service.getAction(projectId, actionIdTyped, customerGet, commonMapper);

    return result;
  }
}

