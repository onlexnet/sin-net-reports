package sinnet.adapters.gql;

import java.util.Objects;
import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import sinnet.app.lib.Functions;
import sinnet.app.ports.in.CustomersPortIn;
import sinnet.app.ports.in.TimeentriesServicePortIn;
import sinnet.domain.models.TimeEntry;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.gql.models.ServiceFilterInputGql;
import sinnet.gql.models.ServiceModelGql;
import sinnet.gql.models.ServicesSearchResultGql;

@Controller
@RequiredArgsConstructor
class ActionsQuerySearch {

  private final TimeentriesServicePortIn service;
  private final CustomersPortIn customerService;
  private final CustomerMapper customerMapper = CustomerMapper.INSTANCE;

  @SchemaMapping
  ServicesSearchResultGql search(ActionsQuery self, @Argument ServiceFilterInputGql filter) {
    @Cleanup
    var event = new ExampleEvent();

    event.begin();
    var requestorEmail = self.primaryEmail();
    var projectId = UUID.fromString(self.projectId());

    var customerList = customerService.customerList(self.projectId(), requestorEmail, customerMapper::toGql);

    var customerGet = Functions.of((String customerId) -> customerList.stream()
        .filter(it -> Objects.equals(customerId, it.getId().getEntityId())).findAny().orElse(null));

    var result = service.search(projectId, filter.getFrom(), filter.getTo()).stream()
        .map(it -> toGql(it, customerGet))
        .toList();
    return new ServicesSearchResultGql()
        .setItems(result);
  }

  private ServiceModelGql toGql(TimeEntry model, java.util.function.Function<String, CustomerEntityGql> customerMapper) {
    var customerId = model.customerId();
    var customer = customerId == null || customerId.isBlank()
        ? null
        : customerMapper.apply(customerId);

    return new ServiceModelGql()
        .setCustomer(customer)
        .setDescription(model.description())
        .setDistance(model.distance())
        .setDuration(model.duration())
        .setEntityId(model.entityId().id().toString())
        .setEntityVersion(model.entityId().tag())
        .setProjectId(model.entityId().projectId().toString())
        .setServicemanEmail(model.servicemanEmail())
        .setServicemanName(model.servicemanName())
        .setWhenProvided(model.whenProvided());
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
