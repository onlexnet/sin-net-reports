package sinnet.gql.api;


import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.app.flow.request.CustomerRemoveCommand;
import sinnet.app.ports.in.CustomersPortIn;
import sinnet.infra.adapters.gql.CustomerMapper;
import sinnet.gql.models.EntityGql;

@Controller
@RequiredArgsConstructor
class CustomersMutationRemove {

  private final CustomersPortIn service;
  private final CustomerMapper customerMapper = CustomerMapper.INSTANCE;
  @SchemaMapping
  public Boolean remove(CustomersMutation self, @Argument EntityGql id) {
    var entityId = customerMapper.map(id);
    var userToken = self.userToken();
    var cmd = new CustomerRemoveCommand(entityId, userToken);
    var result = service.remove(cmd);
    return result.success();
  }
}
