package sinnet.grpc.customers;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.bus.query.FindCustomer;
import sinnet.grpc.customers.GetReply;
import sinnet.grpc.customers.GetRequest;
import sinnet.grpc.mapping.RpcQueryHandler;

@Component
@RequiredArgsConstructor
public class CustomersRpcGet implements
    RpcQueryHandler<GetRequest, GetReply>,
    MapperDto,
    MapperDbo {

  private final CustomerRepository repository;

  @Override
  public GetReply apply(GetRequest request) {
    var projectId = UUID.fromString(request.getEntityId().getProjectId());
    var entityId = UUID.fromString(request.getEntityId().getEntityId());

    var dbo = repository.findByProjectidEntityid(projectId, entityId);
    var result = this.fromDbo(dbo);
    var dto = this.toDto(result);
    return GetReply.newBuilder().setModel(dto).build();
  }

}
