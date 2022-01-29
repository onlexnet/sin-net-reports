package sinnet;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

import io.grpc.examples.helloworld.Ping;
import io.grpc.examples.helloworld.PingRequest;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;

@GraphQLApi
@ApplicationScoped
public class ActionsQuerySearch {

  @GrpcClient("activities")
  Ping hello;

  public @NonNull Uni<ServicesSearchResult> search(@Source ActionsQuery self, ServiceFilter filter) {
    var request = PingRequest.newBuilder().build();
    return hello
      .test(request)
      .map(it -> new ServicesSearchResult());
  }
}
