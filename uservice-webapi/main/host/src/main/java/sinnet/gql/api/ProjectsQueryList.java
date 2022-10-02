package sinnet.gql.api;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

import io.smallrye.mutiny.Uni;
import sinnet.gql.models.CommonMapper;
import sinnet.gql.models.Entity;
import sinnet.gql.models.ProjectEntity;
import sinnet.gql.models.ProjectId;
import sinnet.gql.models.ProjectsQuery;
import sinnet.grpc.GrpcProjects;
import sinnet.grpc.projects.ListRequest;
import sinnet.grpc.projects.Project;
import sinnet.grpc.projects.UserStatsRequest;

@GraphQLApi
public class ProjectsQueryList implements ProjectMapper {

  @Inject
  GrpcProjects service;

  public @NonNull Uni<@NonNull List<ProjectEntity>> list(@Source ProjectsQuery self, @NonNull String name) {
    var request = ListRequest.newBuilder()
        .setEmailOfRequestor(self.getUserToken().getRequestorEmail())
        .build();
    return service.list(request)
        .map(it -> it.getProjectsList().stream().map(this::toDto).collect(Collectors.toList()));
  }

  public @NonNull Uni<@NonNull Integer> numberOfProjects(@Source ProjectsQuery self) {
    var request = UserStatsRequest.newBuilder()
        .setEmailOfRequestor(self.getUserToken().getRequestorEmail())
        .build();
    return service.userStats(request)
        .map(it -> it.getNumberOfProjects());
  }

}

interface ProjectMapper extends CommonMapper {

  default @Nonnull ProjectEntity toDto(@Nonnull Project grpc) {
    var result = new ProjectEntity()
        .setEntity(Entity.builder()
            .entityId(grpc.getId().getEId())
            .projectId(grpc.getId().getEId())
            .entityVersion(grpc.getId().getETag())
            .build())
        .setName(grpc.getModel().getName());
    // the check below is stupid, but removing it creates interesting warning in java related to nullability
    Objects.requireNonNull(result);
    return result;
  }

  default @Nonnull sinnet.grpc.projects.ProjectId toGrpc(@Nonnull ProjectId grpc) {
    var eId = UUID.fromString(grpc.getId());
    var eTag = grpc.getTag();
    return sinnet.grpc.projects.ProjectId.newBuilder()
        .setEId(eId.toString())
        .setETag(eTag)
        .build();
  }

  default @Nonnull ProjectId toGql(@Nonnull sinnet.grpc.projects.ProjectId grpc) {
    return ProjectId.builder()
        .id(grpc.getEId())
        .tag(grpc.getETag())
        .build();
  }
}
