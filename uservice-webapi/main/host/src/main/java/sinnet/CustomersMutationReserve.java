package sinnet;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import sinnet.gql.CommonMapper;
import sinnet.gql.CustomersMutation;
import sinnet.gql.Transform;
import sinnet.grpc.customers.Customers;
import sinnet.grpc.customers.ReserveRequest;

@GraphQLApi
@Slf4j
public class CustomersMutationReserve implements CommonMapper{

  @GrpcClient("activities")
  Customers service;

  public @NonNull Uni<@NonNull Entity> reserve(@Source CustomersMutation self) {
    var request = ReserveRequest.newBuilder()
        .setProjectId(self.getProjectId())
        .build();
    return service.reserve(request)
      .onItemOrFailure()
      .transform(Transform.logged(log, it -> toGql(it.getEntityId())));

  }
}
