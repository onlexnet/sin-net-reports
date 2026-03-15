package sinnet.gql.api;

import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.app.lib.Functions;
import sinnet.app.ports.in.CustomersPortIn;
import sinnet.app.ports.in.TimeentriesServicePortIn;
import sinnet.domain.models.TimeEntry;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.gql.models.ServiceModelGql;
import sinnet.infra.adapters.gql.CustomerMapper;

@Controller
@RequiredArgsConstructor
class ActionsQueryGet {

  private final TimeentriesServicePortIn service;
  private final CustomersPortIn customerService;
  private final CustomerMapper customerMapper = CustomerMapper.INSTANCE;

  @SchemaMapping
  ServiceModelGql get(ActionsQuery self, @Argument String actionId) {
    var projectId = UUID.fromString(self.projectId());
    var actionIdTyped = UUID.fromString(actionId);

    var customerGet = Functions.of((String customerId) ->
        customerMapper.toGql(customerService.customerGet(self.projectId(), self.primaryEmail(), customerId)));
    var result = service.getAction(projectId, actionIdTyped);

    return toGql(result, customerGet);
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
}

