package sinnet.gql.projects;

import static java.util.concurrent.CompletableFuture.completedStage;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLQueryResolver;
import io.grpc.stub.StreamObserver;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import sinnet.IdentityProvider;
import sinnet.gql.Handlers;
import sinnet.grpc.PropsBuilder;
import sinnet.grpc.projects.AvailableProject;
import sinnet.grpc.projects.AvailableProjectsReply;
import sinnet.grpc.projects.AvailableProjectsRequest;
import sinnet.grpc.projects.ProjectsGrpc.ProjectsImplBase;
import sinnet.models.Email;
import sinnet.read.ProjectProjector;

/** Provides multiple projections related to Projects. */
public interface ProjectsQuery {

  @Slf4j
  @Component
  class ProjectsProvider extends ProjectsImplBase {
    @Autowired
    private ProjectProjector.Provider projectProjector;
    @Override
    public void availableProjects(AvailableProjectsRequest request, StreamObserver<AvailableProjectsReply> responseObserver) {
        var invoker = request.getRequestorToken();
        projectProjector
          .findByServiceman(Email.of(invoker))
          .map(it -> it.foldLeft(
            AvailableProjectsReply.newBuilder(),
            (acc, o) -> acc.addProjects(PropsBuilder.build(AvailableProject.newBuilder())
              .tset(o.getId().getId().toString(), b -> b::setId)
              .tset(o.getName(), b -> b::setName)
              .done())))
          .map(it -> it.build())
          .onComplete(Handlers.logged(log, (AvailableProjectsReply it) -> {
            responseObserver.onNext(it);
            responseObserver.onCompleted();
          }));
    }
  }

  
  @Component
  class Resolver implements GraphQLQueryResolver {

    @Autowired
    private IdentityProvider identityProvider;

    @Autowired
    private ProjectProjector.Provider projectProjector;

    private ProjectEntity[] empty = new ProjectEntity[0];

    public CompletionStage<ProjectEntity[]> getAvailableProjects() {
      var maybeUser = identityProvider.getCurrent();
      if (!maybeUser.isPresent()) {
        return completedStage(empty);
      }

      var user = maybeUser.get();
      return projectProjector
          .findByServiceman(Email.of(user.getEmail()))
          .map(it -> it
              .map(o -> new ProjectEntity(o.getId().getId(), o.getName()))
              .toJavaArray(ProjectEntity[]::new))
          .toCompletionStage();
    }
  }

  @Value
  class ProjectEntity {
    private UUID id;
    private String name;
  }
}

