package sinnet.gql.api;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.CustomersPortIn;
import sinnet.domain.models.EntityId;
import sinnet.infra.adapters.gql.EntityGqlMapper;
import sinnet.infra.adapters.grpc.EntityGrpcMapper;
import sinnet.gql.models.EntityGql;
import sinnet.grpc.customers.ReserveRequest;

@Controller
@RequiredArgsConstructor
class CustomersMutationReserve {

  private final CustomersPortIn service;
  private final EntityGrpcMapper entityGrpcMapper = EntityGrpcMapper.INSTANCE;
  private final EntityGqlMapper entityGqlMapper = EntityGqlMapper.INSTANCE;

  @SchemaMapping
  public EntityGql reserve(CustomersMutation self) {
    var request = ReserveRequest.newBuilder()
        .setProjectId(self.projectId().toString())
        .build();
    var result = service.reserve(request);
    var domainEntityId = entityGrpcMapper.fromGrpc(result.getEntityId());

    var normalizedEntityId = domainEntityId.projectId() == null
        ? new EntityId(self.projectId(), domainEntityId.id(), domainEntityId.tag())
        : domainEntityId;

    return entityGqlMapper.toGql(normalizedEntityId);
  }
}
