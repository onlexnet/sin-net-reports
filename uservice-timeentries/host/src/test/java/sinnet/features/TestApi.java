package sinnet.features;

import static sinnet.grpc.timeentries.ReserveCommand.newBuilder;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;

import io.dapr.v1.DaprAppCallbackProtos.TopicEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import sinnet.events.AvroObjectSerializer;
import sinnet.features.RpcApi.UseAlias;
import sinnet.grpc.common.EntityId;
import sinnet.grpc.common.UserToken;
import sinnet.grpc.customers.CustomerModel;
import sinnet.grpc.customers.CustomerModelMapper;
import sinnet.grpc.customers.GetRequest;
import sinnet.grpc.customers.ListRequest;
import sinnet.grpc.customers.MapperDto;
import sinnet.grpc.customers.ReserveRequest;
import sinnet.grpc.customers.UpdateCommand;
import sinnet.grpc.projects.generated.CreateRequest;
import sinnet.grpc.timeentries.LocalDate;
import sinnet.grpc.timeentries.SearchQuery;
import sinnet.grpc.users.IncludeOperatorCommand;
import sinnet.models.CustomerContact;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerValue;
import sinnet.models.ProjectId;
import sinnet.models.ValName;
import sinnet.project.events.ProjectCreatedEvent;

/** Test api allows to invoke well-defined methods available in the system, and updates properly cntext related of the invoking client. */
@RequiredArgsConstructor
@Component
public class TestApi {

  public final RpcApi rpcApi;

  // we use the same deserializer as the whole ecosystem in ser / deser events.
  private final AvroObjectSerializer objectSerializer = new AvroObjectSerializer();

  public void reserveCustomer(ClientContext ctx, ValName operatorAlias, ValName customerAlias) {
    var projectId = "00000000-0000-0000-0001-000000000001";
    var request = ReserveRequest.newBuilder()
        .setProjectId(projectId)
        .build();
    var entityId = rpcApi.getCustomers(UseAlias.of(operatorAlias)).reserve(request).getEntityId();
    rpcApi.getCurrentCustomer(UseAlias.of(customerAlias));
    
    ctx.on(new CustomerReservedAppEvent(customerAlias, entityId));
  }

  public void updateReservedCustomer(ClientContext ctx, ValName customerAlias, CustomerValue customerValue,
  List<CustomerSecret> secrets,
  List<CustomerSecretEx> secretsExt) {
    updateReservedCustomer(ctx, customerAlias, customerValue, List.of(), secrets, secretsExt);
  }

  public void updateReservedCustomer(ClientContext ctx, ValName customerAlias, CustomerValue customerValue,
    List<CustomerContact> contacts,
    List<CustomerSecret> secrets,
    List<CustomerSecretEx> secretsExt) {
    var id = ctx.reservedCustomer;
    var secretsDto = secrets.stream().map(it -> MapperDto.toDto(it)).toList();
    var secretsExtDto = secretsExt.stream().map(it -> MapperDto.toDto(it)).toList();
    var contactsDto = CustomerModelMapper.INSTANCE.toDtoContacts(contacts);
    var cmd = UpdateCommand.newBuilder()
        .setModel(CustomerModel.newBuilder()
          .setId(id)
          .setValue(MapperDto.toDto(customerValue))
          .addAllContacts(contactsDto)
          .addAllSecrets(secretsDto)
          .addAllSecretEx(secretsExtDto))
        .build();
    var result = rpcApi.getCustomers(UseAlias.current()).update(cmd);
    var updatedId = result.getEntityId();

    ctx.on(new CustomerUpdatedAppEvent(customerAlias, id, updatedId, cmd));
  }

  public void updateCustomer(ClientContext ctx, ValName customerAlias, sinnet.models.CustomerModel customer) {
    var customerDto = CustomerModelMapper.INSTANCE.toDto(customer);
    var id = customerDto.getId();
    var cmd = UpdateCommand.newBuilder()
      .setModel(customerDto).build();
    var result = rpcApi.getCustomers(UseAlias.current()).update(cmd);
    var updatedId = result.getEntityId();

    ctx.on(new CustomerUpdatedAppEvent(customerAlias, id, updatedId, cmd));
}

  UserToken newUserToken(ClientContext ctx) {
    var projectId = "00000000-0000-0000-0001-000000000001";
    var operator = rpcApi.getCurrentOperator(UseAlias.current());
    return UserToken.newBuilder()
        .setProjectId(projectId)
        .setRequestorEmail(operator.getValue())
        .build();
  }


  sinnet.models.CustomerModel getCustomer(ClientContext ctx, UseAlias customer, UseAlias invokerOperator) {
    var currentCustomer = rpcApi.getCurrentCustomer(customer);
    var currentOperator = rpcApi.getCurrentOperator(invokerOperator);
    return getCustomer(ctx, currentCustomer, currentOperator);
  }

  sinnet.models.CustomerModel getCustomer(ClientContext ctx, ValName customerAlias, ValName invoker) {
    var id = ctx.reservedCustomer;
    var projectId = "00000000-0000-0000-0001-000000000001";
    var operatorId = ctx.getOperatorId(invoker).value();
    var query = GetRequest.newBuilder()
        .setEntityId(id)
        .setUserToken(UserToken.newBuilder()
          .setProjectId(projectId)
          .setRequestorEmail(operatorId))
        .build();
    var result = rpcApi.getCustomers(UseAlias.of(invoker)).get(query);
    var customerModel = MapperDto.fromDto(result.getModel());
    return customerModel;
  }

  @SneakyThrows
  void newProject(ClientContext ctx, ValName operatorAliasCreator, ValName projectAlias) {

    var operatorId = ctx.getOperatorId(operatorAliasCreator).value();
    var createRequest = CreateRequest.newBuilder()
        .setUserToken(
          sinnet.grpc.projects.generated.UserToken.newBuilder()
            .setRequestorEmail(operatorId)
            .build())
        .build();

    var result = rpcApi.getProjects().create(createRequest);

    var event = ProjectCreatedEvent.newBuilder()
        .setEid(result.getEntityId().getEId())
        .setEtag(result.getEntityId().getETag())
        .build();
    var eventSerialized = objectSerializer.serialize(event);
    var data = ByteString.copyFrom(eventSerialized);
    var te = TopicEventRequest.newBuilder().setData(data).build();
    rpcApi.getApiCallback().onTopicEvent(te);

    var projId = ProjectId.of(
      UUID.fromString(result.getEntityId().getEId()),
      result.getEntityId().getETag());
    ctx.on(new ProjectCreatedAppEvent(projectAlias, projId));
  }

  void assignOperator(ClientContext ctx, ValName operatorAlias, ValName projectAlias) {
    var projectId = ctx.getProjectId(projectAlias);
    var operatorId = ctx.getOperatorId(operatorAlias);
    
    var cmd = IncludeOperatorCommand.newBuilder()
        .setProjectId(projectId.getId().toString())
        .addOperatorEmail(operatorId.value())
        .build();
    rpcApi.getUsers().includeOperator(cmd);

    ctx.on(new OperatorAssignedAppEvent(operatorAlias, projectAlias));
  }

  void createEntry(ClientContext ctx, UseAlias operatorAlias, ValName projectAlias) {
    var projectId = ctx.getProjectId(projectAlias);
    var operatorName = rpcApi.getCurrentOperator(operatorAlias);
    var operatorId = ctx.getOperatorId(operatorName);
    var invoker = UserToken.newBuilder()
        .setProjectId(projectId.getId().toString())
        .setRequestorEmail(operatorId.value());
    var when = ctx.todayAsDto;
    var result = rpcApi.getTimeentries(operatorAlias).reserve(newBuilder()
        .setInvoker(invoker)
        .setWhen(when)
        .build());
    var returnedId = result.getEntityId();

    ctx.newTimeentry(returnedId, when);
  }

  List<EntityId> listTimeentries(ClientContext ctx, ValName projectAlias, LocalDate singleDay) {
    var projectId = ctx.getProjectId(projectAlias);
    var result = rpcApi.getTimeentries(UseAlias.current()).search(SearchQuery.newBuilder()
        .setFrom(singleDay)
        .setTo(singleDay)
        .setProjectId(projectId.getId().toString())
        .build());
    return result.getActivitiesList().stream().map(it -> it.getEntityId()).toList();
  }

  byte[] requestReport1Pack(ClientContext ctx) {
    var request = sinnet.report1.grpc.ReportRequests.newBuilder().build();
    var result = rpcApi.getReports1().producePack(request);
    return result.getData().toByteArray();
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
    var response = rpcApi.getCustomers(UseAlias.of(operatorAlias)).list(req);
    var foundElements = response.getCustomersList().stream()
        .filter(it -> it.getValue().getCustomerName().equals(customerName))
        .count();
    Assertions
      .assertThat(foundElements)
      .as("Expected existence of customer named: [%s]", customerName)
      .isEqualTo(1);
  }

  public List<String> getAllCustomers(ClientContext ctx, ValName operatorAlias) {
    var operatorId = ctx.getOperatorId(operatorAlias).value();
    var projectId = ctx.currentProject.getValue();
    var req = ListRequest.newBuilder()
        .setProjectId(projectId)
        .setUserToken(UserToken.newBuilder().setProjectId(projectId).setRequestorEmail(operatorId))
        .build();
    var response = rpcApi.getCustomers(UseAlias.of(operatorAlias)).list(req);
    return response.getCustomersList().stream().map(it -> it.getValue().getCustomerName()).toList();
  }

  public List<CustomerModel> getAllCustomersData(ClientContext ctx, ValName operatorAlias) {
    var operatorId = ctx.getOperatorId(operatorAlias).value();
    var projectId = "00000000-0000-0000-0001-000000000001";
    var req = ListRequest.newBuilder()
        .setProjectId(projectId)
        .setUserToken(UserToken.newBuilder().setProjectId(projectId).setRequestorEmail(operatorId))
        .build();
    var response = rpcApi.getCustomers(UseAlias.of(operatorAlias)).list(req);
    return response.getCustomersList();
  }

  public int numberOfProjects(ValName operatorAlias) {
    var request = sinnet.grpc.projects.generated.ListRequest.newBuilder()
        .setEmailOfRequestor(operatorAlias.getValue())
        .build();
    var response = rpcApi.getProjects().list(request);
    return response.getProjectsCount();
  }

  /**
   * Adds secrets to lastly updated customer
   * @param ctx
   */
  public void addSecretToUpdatedCustomer(ClientContext ctx, int numberOfSecrets) {
    var customerAlias = rpcApi.getCurrentCustomer(UseAlias.current());
    var idModelDto = ctx.known().customers().get(customerAlias);
    var modelDto = idModelDto._2;
    var asDomain = CustomerModelMapper.INSTANCE.fromDto(modelDto);
    IntStream.range(0, numberOfSecrets).forEach(ignored -> {
      var newSecret = Instancio.create(CustomerSecret.class);
      asDomain.getSecrets().add(newSecret);
    });
    updateCustomer(ctx, customerAlias, asDomain);
  }
}

