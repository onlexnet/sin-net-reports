package sinnet.gql.api;

import javax.inject.Inject;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

import io.smallrye.mutiny.Uni;
import io.vavr.collection.Iterator;
import lombok.extern.slf4j.Slf4j;
import sinnet.gql.Transform;
import sinnet.gql.models.CustomerEntity;
import sinnet.gql.models.CustomersQuery;
import sinnet.grpc.GrpcCustomers;
import sinnet.grpc.customers.ListRequest;

@GraphQLApi
@Slf4j
public class CustomersQueryList implements CustomerMapper {

  @Inject GrpcCustomers service;

  public @NonNull Uni<@NonNull CustomerEntity[]> list(@Source CustomersQuery self) {
    var request = ListRequest.newBuilder()
        .setProjectId(self.getProjectId())
        .setUserToken(self.getUserToken())
        .build();
    return service.list(request)
        .onItemOrFailure()
        .transform(Transform.logged(log, it -> {
          return Iterator.ofAll(it.getCustomersList()).map(this::toGql).toJavaArray(CustomerEntity[]::new);
        }));
  }

}
