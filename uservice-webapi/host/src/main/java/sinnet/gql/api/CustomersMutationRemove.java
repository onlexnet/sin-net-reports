package sinnet.gql.api;


import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.gql.models.EntityGql;
import sinnet.grpc.customers.RemoveRequest;
import sinnet.ports.timeentries.CustomersGrpcFacade;

@Controller
@RequiredArgsConstructor
class CustomersMutationRemove {

  private final CustomersGrpcFacade service;
  private final CustomerMapper customerMapper;

  @SchemaMapping
  public Boolean remove(CustomersMutation self, @Argument EntityGql id) {
    var cmd = RemoveRequest.newBuilder()
        .setEntityId(customerMapper.toGrpc(id))
        .setUserToken(self.getUserToken())
        .build();
    var result = service.remove(cmd);
    return result.getSuccess();
  }
}
