package sinnet.infra.adapters.gql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.app.flow.request.CustomerGetQuery;
import sinnet.app.ports.in.CustomersPortIn;
import sinnet.gql.models.CustomerEntityGql;

@Controller
@RequiredArgsConstructor
class CustomersQueryGet {

  private final CustomersPortIn service;
  private final CustomerMapper customerMapper = CustomerMapper.INSTANCE;

  @SchemaMapping
  public CustomerEntityGql get(CustomersQuery self, @Argument String entityId) {
    var query = new CustomerGetQuery(self.userToken(), entityId);
    var result = service.get(query);
    return customerMapper.toGql(result);
  }

}
