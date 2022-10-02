package sinnet.gql.api;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Source;

import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import sinnet.gql.ServiceModel;
import sinnet.gql.TimeentriesMapper;
import sinnet.gql.Transform;
import sinnet.gql.models.ActionsQuery;
import sinnet.grpc.timeentries.GetQuery;
import sinnet.grpc.timeentries.TimeEntries;

@GraphQLApi
@Slf4j
public class ActionsQueryGet implements TimeentriesMapper {

  @GrpcClient("activities")
  TimeEntries service;

  public Uni<ServiceModel> get(@Source ActionsQuery self, @Id String actionId) {
    var request = GetQuery.newBuilder()
        .setProjectId(self.getProjectId())
        .setTimeentryId(actionId)
        .build();
    return service.get(request)
        .onItemOrFailure()
        .transform(Transform.logged(log, it -> {
          return toGql(it, self.getUserToken());
        }));
  }
}
