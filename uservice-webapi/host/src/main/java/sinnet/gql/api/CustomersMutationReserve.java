package sinnet.gql.api;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.gql.models.EntityGql;
import sinnet.grpc.customers.ReserveRequest;
import sinnet.ports.timeentries.CustomersGrpcFacade;

@Controller
@RequiredArgsConstructor
class CustomersMutationReserve {

  private final CustomersGrpcFacade service;
  private final CommonMapper commonMapper;

  @SchemaMapping
  public EntityGql reserve(CustomersMutation self) {
    var request = ReserveRequest.newBuilder()
        .setProjectId(self.getProjectId())
        .build();
    var result = service.reserve(request);
    return commonMapper.toGql(result.getEntityId());
  }
}
