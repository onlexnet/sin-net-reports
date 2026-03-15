package sinnet.infra.adapters.grpc;

import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.app.flow.request.CustomerGetQuery;
import sinnet.app.flow.request.CustomerGetResult;
import sinnet.app.flow.request.CustomerListQuery;
import sinnet.app.flow.request.CustomerListResult;
import sinnet.app.flow.request.CustomerReserveCommand;
import sinnet.app.flow.request.CustomerUpdateCommand;
import sinnet.app.ports.out.CustomersPortOut;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.grpc.common.EntityId;
import sinnet.grpc.common.UserToken;
import sinnet.grpc.customers.CustomerModel;
import sinnet.grpc.customers.CustomersGrpc.CustomersBlockingStub;
import sinnet.grpc.customers.GetReply;
import sinnet.grpc.customers.GetRequest;
import sinnet.grpc.customers.ListRequest;
import sinnet.grpc.customers.RemoveReply;
import sinnet.grpc.customers.RemoveRequest;
import sinnet.grpc.customers.ReserveReply;
import sinnet.grpc.customers.ReserveRequest;
import sinnet.grpc.customers.UpdateCommand;
import sinnet.grpc.customers.UpdateResult;

/** Mockable equivalent of {@link ProjectsGrpcStub}. */
@Component
@RequiredArgsConstructor
class CustomersGateway implements CustomersPortOut {

  private final CustomersBlockingStub stub;


  @Override
  public CustomerListResult list(CustomerListQuery query) {
    var request = ListRequest.newBuilder()
        .setProjectId(query.userToken().projectId().toString())
        .setUserToken(Map.apply.map(query.userToken()))
        .build();
    var reply = stub.list(request);
    return new CustomerListResult(reply.getCustomersList());
  }

  @Override
  public CustomerGetResult get(CustomerGetQuery query) {
    var request = GetRequest.newBuilder()
        .setEntityId(EntityId.newBuilder()
            .setProjectId(query.userToken().projectId().toString())
            .setEntityId(query.entityId())
            .build())
        .setUserToken(Map.apply.map(query.userToken()))
        .build();
    var reply = stub.get(request);
    return new CustomerGetResult(reply.getModel());
  }

  @Override
  public ReserveReply reserve(CustomerReserveCommand cmd) {
    var request = ReserveRequest.newBuilder()
        .setProjectId(cmd.projectId().toString())
        .build();
    return stub.reserve(request);
  }

  @Override
  public RemoveReply remove(sinnet.domain.models.EntityId customerId, sinnet.domain.models.UserToken requestor) {
    var req = RemoveRequest.newBuilder()
        .setEntityId(Map.apply.map(customerId))
        .setUserToken(Map.apply.map(requestor))
        .build();
    return stub.remove(req);
  }

  @Override
  public UpdateResult update(CustomerUpdateCommand request) {
    var changedWhen = request.changedWhen();
    var changedWho = request.changedWho();
    var userToken = Map.apply.map(request.userToken());
    var grpcRequest = UpdateCommand.newBuilder()
        .setUserToken(userToken)
        .setModel(CustomerModel.newBuilder()
          .setId(CustomerMapper.toGrpc(request.id()))
          .setValue(CustomerMapper.toGrpc(request.value().entry()))
          .addAllSecrets(request.value().secrets().stream().map(it -> CustomerMapper.toGrpc(it, changedWhen, changedWho)).toList())
          .addAllSecretEx(request.value().secretsEx().stream().map(it -> CustomerMapper.toGrpc(it, changedWhen, changedWho)).toList())
          .addAllContacts(request.value().contacts().stream().map(CustomerMapper::toGrpc).toList())
            .build())
        .build();
    return stub.update(grpcRequest);
  }


  /** Doxme. */
  @Override
  public CustomerEntityGql customerGet(String projectId, String requestorEmail, String customerId, Function<GetReply, CustomerEntityGql> mapper) {
    var entityId = EntityId.newBuilder()
        .setEntityId(customerId)
        .setEntityVersion(0)
        .setProjectId(projectId)
        .build();
    var userToken = UserToken.newBuilder()
        .setProjectId(projectId)
        .setRequestorEmail(requestorEmail)
        .build();
    var request = GetRequest.newBuilder()
        .setEntityId(entityId)
        .setUserToken(userToken)
        .build();
    var result = stub.get(request);
    return mapper.apply(result);
  }

  /** Doxme. */
  @Override
  public <T> List<T> customerList(String projectId, String requestorEmail, Function<sinnet.grpc.customers.CustomerModel, T> mapper) {
    var userToken = UserToken.newBuilder()
        .setProjectId(projectId)
        .setRequestorEmail(requestorEmail)
        .build();
    var request = ListRequest.newBuilder()
        .setProjectId(projectId)
        .setUserToken(userToken)
        .build();
    var result = stub.list(request).getCustomersList().stream().map(mapper).toList();
    return result;
  }

}
