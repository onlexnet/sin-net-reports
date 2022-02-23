package sinnet.gql;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Source;

import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import sinnet.CustomerEntity;
import sinnet.grpc.common.EntityId;
import sinnet.grpc.customers.Customers;
import sinnet.grpc.customers.GetRequest;

@GraphQLApi
@ApplicationScoped
@Slf4j
public class ActionsQueryGetServiceModel implements CustomerMapper {

  @GrpcClient("activities")
  Customers service;

  public Uni<CustomerEntity> getCustomer(@Source ServiceModel self) {
    var entity = EntityId.newBuilder()
        .setProjectId(self.getProjectId())
        .setEntityId(self.getCustomerId())
        .setEntityVersion(0);

    var request = GetRequest.newBuilder()
        .setEntityId(entity)
        .setUserToken(self.getUserToken())
        .build();
    return service.get(request)
        .onItemOrFailure()
        .transform(Transform.logged(log, it -> {
          return toGql(it);
        }));
  }
}
