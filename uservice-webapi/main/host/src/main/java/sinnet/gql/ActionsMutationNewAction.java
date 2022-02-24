package sinnet.gql;

import java.time.LocalDate;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import sinnet.ActionsMutation;
import sinnet.gql.models.Entity;
import sinnet.grpc.timeentries.ReserveCommand;
import sinnet.grpc.timeentries.TimeEntries;

@GraphQLApi
public class ActionsMutationNewAction implements Mapper {

  @GrpcClient("activities")
  TimeEntries service;

  public @NonNull Uni<Entity> newAction(@Source ActionsMutation self, @NonNull LocalDate whenProvided) {
    var cmd = ReserveCommand.newBuilder()
        .setInvoker(self.getUserToken())
        .setWhen(toGrpc(whenProvided))
        .build();
    return service
      .reserve(cmd)
      .map(this::toGql);
  }
}
