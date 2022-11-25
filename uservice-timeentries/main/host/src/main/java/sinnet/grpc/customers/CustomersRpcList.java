package sinnet.grpc.customers;

import static sinnet.grpc.mapping.PropsBuilder.ofNullable;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import io.vavr.collection.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.grpc.common.EntityId;
import sinnet.grpc.customers.ListReply;
import sinnet.grpc.customers.ListRequest;
import sinnet.grpc.mapping.PropsBuilder;
import sinnet.grpc.mapping.RpcQueryHandler;
import sinnet.models.CustomerModel;

@Component
@RequiredArgsConstructor
@Slf4j
class CustomersRpcList implements RpcQueryHandler<ListRequest, ListReply>,
                       MapperDto,
                       sinnet.grpc.common.Mapper, MapperDbo {

  private final CustomerRepository repository;

  @Override
  public ListReply apply(ListRequest request) {
    var projectId = UUID.fromString(request.getProjectId());
    var result = repository.findByProjectid(projectId).map(this::fromDbo);
    var response = result.map(this::toDto).toList();

    return ListReply.newBuilder()
        .addAllCustomers(response)
        .build();
  }

}
