package sinnet.grpc.customers;

import java.util.UUID;

import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(TxType.REQUIRES_NEW)
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
