package sinnet;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.hibernate.reactive.mutiny.Mutiny;

import com.google.common.base.Objects;

import io.grpc.Status;
import io.smallrye.mutiny.Uni;
import io.vavr.Function1;
import io.vavr.control.Either;
import sinnet.grpc.projects.RemoveCommand;
import sinnet.grpc.projects.RemoveResult;

interface ProjectsRemove {
  Uni<RemoveResult> remove(RemoveCommand request);
}

@ApplicationScoped
class ProjectsRemoveImpl implements ProjectsRemove {

  @Inject
  ProjectRepository projectsRepo;

  @Inject
  Mutiny.SessionFactory factory;
  
  private Function1<ProjectDbo, Either<Exception, ProjectDbo>> guardRole(String expectedOwner) {
    return dbo -> Objects.equal(dbo.getEmailOfOwner(), expectedOwner)
      ? Either.right(dbo)
      : Either.left(Status.FAILED_PRECONDITION.withDescription("Illegal owner").asException());
  }

  private Uni<? extends ProjectDbo> guardRole(Either<Exception, ProjectDbo> it) {
    return it.isRight()
      ? Uni.createFrom().item(it.get())
      : Uni.createFrom().failure(it.getLeft());
  }

  @Override
  public Uni<RemoveResult> remove(RemoveCommand request) {
    var eIdAsString = request.getProjectId().getEId();
    var eId = UUID.fromString(eIdAsString);
    var expectedOwner = request.getUserToken().getRequestorEmail();

    return factory.withTransaction(
      (session, tx) -> session.find(ProjectDbo.class, eId)

        .map(guardRole(expectedOwner))
        .flatMap(this::guardRole)

        .flatMap(session::remove)
        .flatMap(it -> session.flush())
        .map(it -> RemoveResult.newBuilder().build()));
  }

}
