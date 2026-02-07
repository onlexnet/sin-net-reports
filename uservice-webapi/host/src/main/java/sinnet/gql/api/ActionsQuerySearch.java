package sinnet.gql.api;

import java.util.Objects;
import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import io.vavr.Function1;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import sinnet.gql.models.ServiceFilterInputGql;
import sinnet.gql.models.ServicesSearchResultGql;
import sinnet.ports.timeentries.ActionsGrpcFacade;
import sinnet.ports.timeentries.CustomersGrpcFacade;

@Controller
@RequiredArgsConstructor
class ActionsQuerySearch {

  private final ActionsGrpcFacade service;
  private final CustomersGrpcFacade customerService;
  private final CustomerMapper customerMapper;
  private final CommonMapper commonMapper;

  @SchemaMapping
  ServicesSearchResultGql search(ActionsQuery self, @Argument ServiceFilterInputGql filter) {
    @Cleanup
    var event = new ExampleEvent();

    event.begin();
    var requestorEmail = self.primaryEmail();
    var projectId = UUID.fromString(self.projectId());

    var customerList = customerService.customerList(self.projectId(), requestorEmail, customerMapper::toGql);

    var customerGet = Function1.of((String customerId) -> customerList.stream()
        .filter(it -> Objects.equals(customerId, it.getId().getEntityId())).findAny().orElse(null));

    var result = service.search(projectId, filter.getFrom(), filter.getTo(), customerGet, commonMapper);
    return new ServicesSearchResultGql()
        .setItems(result);
  }

  static class ExampleEvent extends jdk.jfr.Event implements AutoCloseable {

    public ExampleEvent() {
      super.begin();
    }

    @Override
    public void close() {
      super.end();
      super.commit();
    }
  }
}
