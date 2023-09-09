package sinnet.grpc.customers;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class CustomersRpcListImpl implements CustomersRpcList,
                           MapperDto,
                           MapperDbo {

  private final CustomerRepository repository;

  @Override
  public ListReply apply(ListRequest request) {
    var projectId = UUID.fromString(request.getProjectId());
    var result = repository.findByProjectId(projectId);
    var response = result.map(this::fromDbo).map(this::toDto).toList();

    return ListReply.newBuilder()
        .addAllCustomers(response)
        .build();
  }

}
