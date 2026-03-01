package sinnet.gql.api;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.CustomersInPort;
import sinnet.gql.models.EntityGql;
import sinnet.grpc.customers.ReserveRequest;

@Controller
@RequiredArgsConstructor
class CustomersMutationReserve {

  private final CustomersInPort service;
  private final CommonMapper commonMapper;

  @SchemaMapping
  public EntityGql reserve(CustomersMutation self) {
    var request = ReserveRequest.newBuilder()
        .setProjectId(self.projectId())
        .build();
    var result = service.reserve(request);
    return commonMapper.toGql(result.getEntityId());
  }
}
