package sinnet.gql.api;


import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import io.vavr.collection.Iterator;
import lombok.RequiredArgsConstructor;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.grpc.CustomersGrpcFacade;
import sinnet.grpc.customers.ListRequest;

@Controller
@RequiredArgsConstructor
class CustomersQueryList implements CustomerMapper {

  private final CustomersGrpcFacade service;

  @SchemaMapping
  public CustomerEntityGql[] list(CustomersQuery self) {
    var request = ListRequest.newBuilder()
        .setProjectId(self.userToken().getProjectId())
        .setUserToken(self.userToken())
        .build();
    var items = service.list(request);
    return Iterator.ofAll(items.getCustomersList()).map(it -> this.toGql(it, ignored -> null)).toJavaArray(CustomerEntityGql[]::new);
  }

}
