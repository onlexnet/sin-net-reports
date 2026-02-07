package sinnet.gql.api;


import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import io.vavr.collection.Iterator;
import lombok.RequiredArgsConstructor;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.grpc.customers.ListRequest;
import sinnet.ports.timeentries.CustomersGrpcFacade;

@Controller
@RequiredArgsConstructor
class CustomersQueryList {

  private final CustomersGrpcFacade service;
  private final CustomerMapper customerMapper;

  @SchemaMapping
  public CustomerEntityGql[] list(CustomersQuery self) {
    var request = ListRequest.newBuilder()
        .setProjectId(self.userToken().getProjectId())
        .setUserToken(self.userToken())
        .build();
    var items = service.list(request);
    return Iterator.ofAll(items.getCustomersList()).map(customerMapper::toGql).toJavaArray(CustomerEntityGql[]::new);
  }

}
