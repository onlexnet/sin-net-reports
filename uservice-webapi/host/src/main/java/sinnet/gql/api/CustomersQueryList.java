package sinnet.gql.api;


import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.CustomersPortIn;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.grpc.customers.ListRequest;

@Controller
@RequiredArgsConstructor
class CustomersQueryList {

  private final CustomersPortIn service;
  private final CustomerMapper customerMapper;

  @SchemaMapping
  public CustomerEntityGql[] list(CustomersQuery self) {
    var request = ListRequest.newBuilder()
        .setProjectId(self.userToken().getProjectId())
        .setUserToken(self.userToken())
        .build();
    var items = service.list(request);
    return items.getCustomersList().stream().map(customerMapper::toGql).toArray(CustomerEntityGql[]::new);
  }

}
