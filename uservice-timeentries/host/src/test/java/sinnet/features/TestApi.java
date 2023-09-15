package sinnet.features;

import static sinnet.grpc.timeentries.ReserveCommand.newBuilder;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;

import io.dapr.v1.DaprAppCallbackProtos.TopicEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import sinnet.events.AvroObjectSerializer;
import sinnet.grpc.common.EntityId;
import sinnet.grpc.common.UserToken;
import sinnet.grpc.customers.CustomerModel;
import sinnet.grpc.customers.CustomerValue;
import sinnet.grpc.customers.ListRequest;
import sinnet.grpc.customers.ReserveRequest;
import sinnet.grpc.customers.UpdateCommand;
import sinnet.grpc.timeentries.LocalDate;
import sinnet.grpc.timeentries.SearchQuery;
import sinnet.grpc.users.IncludeOperatorCommand;
import sinnet.models.ValName;
import sinnet.project.events.ProjectCreatedEvent;

@RequiredArgsConstructor
@Component
public class TestApi {

  private final RpcApi rpcApi;

  // we use the same deserializer as the whole ecosystem in ser / deser events.
  private final AvroObjectSerializer objectSerializer = new AvroObjectSerializer();

  void reserveCustomer(ClientContext ctx, ValName customerAlias) {
    var projectId = "00000000-0000-0000-0001-000000000001";
    var request = ReserveRequest.newBuilder()
        .setProjectId(projectId)
        .build();
    var entityId = rpcApi.getCustomers().reserve(request).getEntityId();
    
    ctx.on(new CustomerReservedEvent(customerAlias, entityId));
  }

  void updateCustomer(ClientContext ctx, ValName customerAlias, String newName) {
    var id = ctx.reservedCustomer;
    var cmd = UpdateCommand.newBuilder()
        .setModel(CustomerModel.newBuilder()
          .setId(id)
          .setValue(CustomerValue.newBuilder()
            .setCustomerName(newName)))
        .build();
    var result = rpcApi.getCustomers().update(cmd);
    var updatedId = result.getEntityId();

    ctx.on(new CustomerUpdatedEvent(customerAlias, id, updatedId, cmd));
  }

  @SneakyThrows
  void notifyNewProject(ClientContext ctx, ValName projectAlias) {
    var projectId = ctx.setProjectId(projectAlias);
    var event = ProjectCreatedEvent.newBuilder()
        .setEid(projectId.getId().toString())
        .setEtag(projectId.getVersion())
        .build();
    var eventSerialized = objectSerializer.serialize(event);
    var data = ByteString.copyFrom(eventSerialized);
    var te = TopicEventRequest.newBuilder().setData(data).build();
    rpcApi.getApiCallback().onTopicEvent(te);
  }

  void assignOperator(ClientContext ctx, ValName operatorAlias, ValName projectAlias) {
    var projectId = ctx.getProjectId(projectAlias);
    var operatorId = ctx.known().users().get(operatorAlias);
    var cmd = IncludeOperatorCommand.newBuilder()
        .setProjectId(projectId.getId().toString())
        .addOperatorEmail(operatorId.value())
        .build();
    rpcApi.getUsers().includeOperator(cmd);

    ctx.on(new OperatorAssignedEvent(operatorAlias, projectAlias));
  }

  void createEntry(ClientContext ctx, ValName projectAlias, ValName operatorAlias) {
    var projectId = ctx.getProjectId(projectAlias);
    var operatorId = ctx.known().users().get(operatorAlias);
    var invoker = UserToken.newBuilder()
        .setProjectId(projectId.getId().toString())
        .setRequestorEmail(operatorId.value());
    var when = ctx.todayAsDto;
    var result = rpcApi.getTimeentries().reserve(newBuilder()
        .setInvoker(invoker)
        .setWhen(when)
        .build());
    var returnedId = result.getEntityId();
    ctx.newTimeentry(returnedId, when);
  }

  List<EntityId> listTimeentries(ClientContext ctx, ValName projectAlias, LocalDate singleDay) {
    var projectId = ctx.getProjectId(projectAlias);
    var result = rpcApi.getTimeentries().search(SearchQuery.newBuilder()
        .setFrom(singleDay)
        .setTo(singleDay)
        .setProjectId(projectId.getId().toString())
        .build());
    return result.getActivitiesList().stream().map(it -> it.getEntityId()).toList();
  }

  public void customerExists(ClientContext ctx, ValName operatorAlias, String customerName) {
    var customerCtx = ctx.known().customers().entrySet().stream()
        .filter(it -> it.getValue()._2.getValue().getCustomerName().equals(customerName))
        .findAny()
        .get();
    var projectId = customerCtx.getValue()._1.getProjectId();
    var operatorId = ctx.getOperatorId(operatorAlias).value();
    var req = ListRequest.newBuilder()
        .setProjectId(projectId)
        .setUserToken(UserToken.newBuilder().setProjectId(projectId).setRequestorEmail(operatorId))
        .build();
    var response = rpcApi.getCustomers().list(req);
    var foundElements = response.getCustomersList().stream()
        .filter(it -> it.getValue().getCustomerName().equals(customerName))
        .count();
    Assertions
      .assertThat(foundElements)
      .as("Expected existence of customer named: [%s]", customerName)
      .isEqualTo(1);
  }

}


