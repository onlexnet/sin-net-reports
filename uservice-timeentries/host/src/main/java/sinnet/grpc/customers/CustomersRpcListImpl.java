package sinnet.grpc.customers;

import java.util.UUID;

import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class CustomersRpcListImpl implements CustomersRpcList, MapperDbo {

  private final CustomerRepository repository;
  private final EntityManager em;
  
  @Override
  public ListReply apply(ListRequest request) {
    var projectId = UUID.fromString(request.getProjectId());

    var jpql = "SELECT c FROM CustomerDbo c "
        + "WHERE c.projectId = :projectId";

    // var result = repository.findByProjectId(projectId);
    var result = em.createQuery(jpql, CustomerRepository.CustomerDbo.class)
        .setParameter("projectId", projectId)
        .getResultList();

    var response = result.stream().map(this::fromDbo).map(MapperDto::toDto).toList();

    return ListReply.newBuilder()
        .addAllCustomers(response)
        .build();
  }

}
