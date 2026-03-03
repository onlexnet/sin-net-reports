package sinnet.infra.adapters.grpc;

import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.app.flow.request.CustomerUpdateCommand;
import sinnet.app.ports.out.CustomersPortOut;
import sinnet.gql.api.CustomerMapper;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.grpc.common.EntityId;
import sinnet.grpc.common.UserToken;
import sinnet.grpc.customers.CustomerModel;
import sinnet.grpc.customers.CustomersGrpc.CustomersBlockingStub;
import sinnet.grpc.customers.GetReply;
import sinnet.grpc.customers.GetRequest;
import sinnet.grpc.customers.ListReply;
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
public class CustomersGrpcFacade implements CustomersPortOut {

  private final CustomerMapper customerMapper;

  private final CustomersBlockingStub stub;


  @Override
  public ListReply list(ListRequest request) {
    return stub.list(request);
  }

  @Override
  public GetReply get(GetRequest request) {
    return stub.get(request);
  }

  @Override
  public ReserveReply reserve(ReserveRequest request) {
    return stub.reserve(request);
  }

  @Override
  public RemoveReply remove(RemoveRequest request) {
    return stub.remove(request);
  }

  @Override
  public UpdateResult update(CustomerUpdateCommand request) {
    var changedWhen = request.changedWhen();
    var changedWho = request.changedWho();
    var userToken = Map.apply.map(request.userToken());
    var grpcRequest = UpdateCommand.newBuilder()
        .setUserToken(userToken)
        .setModel(CustomerModel.newBuilder()
            .setId(customerMapper.toGrpc(request.id()))
            .setValue(customerMapper.toGrpc(request.value().entry()))
            .addAllSecrets(request.value().secrets().stream().map(it -> customerMapper.toGrpc(it, changedWhen, changedWho)).toList())
            .addAllSecretEx(request.value().secretsEx().stream().map(it -> customerMapper.toGrpc(it, changedWhen, changedWho)).toList())
            .addAllContacts(request.value().contacts().stream().map(customerMapper::toGrpc).toList())
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
    var result = get(request);
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
    var result = list(request).getCustomersList().stream().map(mapper).toList();
    return result;
  }

}
