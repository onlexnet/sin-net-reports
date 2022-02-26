package sinnet.gql.api;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import sinnet.gql.Transform;
import sinnet.gql.models.Entity;
import sinnet.grpc.customers.Customers;
import sinnet.grpc.customers.RemoveReply;
import sinnet.grpc.customers.RemoveRequest;

@GraphQLApi
@Slf4j
public class CustomersMutationRemove implements CustomerMapper {

  @GrpcClient("activities")
  Customers service;

  public @NonNull Uni<@NonNull Boolean> remove(@Source CustomersMutation self, @NonNull Entity id) {
    var cmd = RemoveRequest.newBuilder()
        .setEntityId(toGrpc(id))
        .setUserToken(self.getUserToken())
        .build();
    return service.remove(cmd)
        .onItemOrFailure()
        .transform(Transform.logged(log, RemoveReply::getSuccess));
  }
}
