package sinnet.grpc.customers;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class CustomersRpcListImpl implements CustomersRpcList {

  private final CustomerJdbcRepository repository;
  
  @Override
  public ListReply apply(ListRequest request) {
    var projectId = UUID.fromString(request.getProjectId());

    var result = repository.findByProjectId(projectId);
    var response = result.stream().map(CustomerMapper.INSTANCE::fromDbo1).map(MapperDto::toDto).toList();

    return ListReply.newBuilder()
        .addAllCustomers(response)
        .build();
  }

}
