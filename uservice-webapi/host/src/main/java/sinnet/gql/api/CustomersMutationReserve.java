package sinnet.gql.api;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.app.flow.request.CustomerReserveCommand;
import sinnet.app.ports.in.CustomersPortIn;
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
    var cmd = new CustomerReserveCommand(self.projectId());
    var result = service.reserve(cmd);
    return entityGqlMapper.toGql(entityGrpcMapper.fromGrpc(result.getEntityId()));
  }
}
