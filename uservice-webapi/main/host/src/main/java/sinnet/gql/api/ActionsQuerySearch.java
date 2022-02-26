package sinnet.gql.api;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import io.vavr.collection.List;
import lombok.extern.slf4j.Slf4j;
import sinnet.ActionsQuery;
import sinnet.ServiceFilter;
import sinnet.ServicesSearchResult;
import sinnet.gql.ServiceModel;
import sinnet.gql.TimeentriesMapper;
import sinnet.gql.Transform;
import sinnet.grpc.timeentries.SearchQuery;
import sinnet.grpc.timeentries.TimeEntries;

@GraphQLApi
@Slf4j
public class ActionsQuerySearch implements TimeentriesMapper {

  @GrpcClient("activities")
  TimeEntries service;

  public Uni<@NonNull ServicesSearchResult> search(@Source ActionsQuery self, ServiceFilter filter) {
    var query = SearchQuery.newBuilder()
        .setProjectId(self.getProjectId())
        .setFrom(toGrpc(filter.getFrom()))
        .setTo(toGrpc(filter.getTo()))
        .build();
    return service.search(query)
      .onItemOrFailure()
      .transform(Transform.logged(log, it -> List
        .ofAll(it.getActivitiesList())
        .map(o -> {
          var item = new ServiceModel();
          item.setProjectId(o.getEntityId().getProjectId());
          item.setEntityId(o.getEntityId().getEntityId());
          item.setEntityVersion(o.getEntityId().getEntityVersion());
          item.setServicemanName(o.getServicemanName());
          item.setWhenProvided(toGql(o.getWhenProvided()));
          item.setDescription(o.getDescription());
          item.setDistance(o.getDistance());
          item.setDuration(o.getDuration());
          return item;
        }).toJavaArray(ServiceModel[]::new)
      ))
      .map(it -> {
          var distance = 10;
          var result = new ServicesSearchResult();
          result.setItems(it);
          result.setTotalDistance(distance);
          return result;
      });
  }
}
