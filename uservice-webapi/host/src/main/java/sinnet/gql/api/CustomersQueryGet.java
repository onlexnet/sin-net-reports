package sinnet.gql.api;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.CustomersPortIn;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.grpc.common.EntityId;
import sinnet.grpc.customers.GetRequest;

@Controller
@RequiredArgsConstructor
class CustomersQueryGet {

  private final CustomersPortIn service;
  private final CustomerMapper customerMapper = CustomerMapper.INSTANCE;

  @SchemaMapping
  public CustomerEntityGql get(CustomersQuery self, @Argument String entityId) {
    var request = GetRequest.newBuilder()
        .setEntityId(EntityId.newBuilder()
            .setProjectId(self.userToken().getProjectId())
            .setEntityId(entityId))
        .build();

    var result = service.get(request);

    return customerMapper.toGql(result);
  }

}
