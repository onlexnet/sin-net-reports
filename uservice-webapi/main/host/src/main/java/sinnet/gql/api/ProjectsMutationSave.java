package sinnet.gql.api;

import javax.inject.Inject;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

import io.smallrye.common.constraint.NotNull;
import io.smallrye.mutiny.Uni;
import sinnet.gql.models.CommonMapper;
import sinnet.gql.models.Entity;
import sinnet.gql.models.ProjectEntity;
import sinnet.gql.models.ProjectsMutation;
import sinnet.grpc.GrpcProjects;
import sinnet.grpc.projects.ProjectModel;
import sinnet.grpc.projects.ReserveReply;
import sinnet.grpc.projects.ReserveRequest;
import sinnet.grpc.projects.UpdateCommand;
import sinnet.grpc.projects.UpdateResult;

@GraphQLApi
public class ProjectsMutationSave implements ProjectMapper {

  @Inject
  GrpcProjects service;

  public @NonNull Uni<@NonNull ProjectEntity> save(@Source ProjectsMutation self, @NonNull String name) {
    var reserveReq = ReserveRequest.newBuilder().build();
    return service.reserve(reserveReq)
        .map(ReserveReply::getEntityId)
        .map(it -> UpdateCommand.newBuilder()
          .setEntityId(it)
          .setModel(ProjectModel.newBuilder()
            .setEmailOfOwner(self.getUserToken().getRequestorEmail())
            .setName(name))
          .build())
        .flatMap(service::update)
        .map(UpdateResult::getEntityId)
        .map(it -> new ProjectEntity()
              .setEntity(Entity.builder()
                .entityId(it.getEId())
                .projectId(it.getEId())
                .entityVersion(it.getETag())
                .build())
              .setName(name));
  }
}
