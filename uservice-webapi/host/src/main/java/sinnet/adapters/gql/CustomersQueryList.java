package sinnet.adapters.gql;


import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.app.flow.request.CustomerListQuery;
import sinnet.app.ports.in.CustomersPortIn;
import sinnet.gql.models.CustomerEntityGql;

@Controller
@RequiredArgsConstructor
class CustomersQueryList {

  private final CustomersPortIn service;
  private final CustomerMapper customerMapper = CustomerMapper.INSTANCE;

  @SchemaMapping
  public CustomerEntityGql[] list(CustomersQuery self) {
    var query = new CustomerListQuery(self.userToken());
    var items = service.list(query);
    return items.customers().stream().map(customerMapper::toGql).toArray(CustomerEntityGql[]::new);
  }

}
