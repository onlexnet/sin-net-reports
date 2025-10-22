package sinnet.ports.timeentries;

import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.grpc.common.EntityId;
import sinnet.grpc.common.UserToken;
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
public class CustomersGrpcFacade {

  private interface CustomersService {

    ListReply list(ListRequest request);
    
    GetReply get(GetRequest request);

    ReserveReply reserve(ReserveRequest request);

    RemoveReply remove(RemoveRequest request);

    UpdateResult update(UpdateCommand request);
  }

  /** Doxme. */
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

  @Delegate(types = CustomersService.class)
  private final CustomersBlockingStub stub;


}
