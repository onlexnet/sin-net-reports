package sinnet.gql.api;

import javax.inject.Inject;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import sinnet.gql.Transform;
import sinnet.gql.models.CommonMapper;
import sinnet.grpc.GrpcCustomers;
import sinnet.grpc.customers.ReserveRequest;

@GraphQLApi
@Slf4j
public class CustomersMutationReserve implements CommonMapper{

  @Inject GrpcCustomers service;

  public @NonNull Uni<sinnet.gql.models.Entity> reserve(@Source CustomersMutation self) {
    var request = ReserveRequest.newBuilder()
        .setProjectId(self.getProjectId())
        .build();
    return service.reserve(request)
      .onItemOrFailure()
      .transform(Transform.logged(log, it -> toGql(it.getEntityId())));

  }
}
