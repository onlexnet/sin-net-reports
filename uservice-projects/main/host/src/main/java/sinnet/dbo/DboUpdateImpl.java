package sinnet.dbo;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.mutiny.Mutiny.Session;

import io.grpc.Status;
import io.smallrye.mutiny.Uni;
import io.vavr.Function1;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.val;
import sinnet.grpc.projects.ProjectId;
import sinnet.grpc.projects.UpdateCommand;
import sinnet.grpc.projects.UpdateResult;

@ApplicationScoped
@RequiredArgsConstructor
class DboUpdateImpl implements DboUpdate {

  private final Mutiny.SessionFactory factory;
  
  @Override
  public Uni<UpdateResult> update(UpdateCommand request) {
    var eidAsString = request.getEntityId().getEId();
    var eid = UUID.fromString(eidAsString);
    var etag = request.getEntityId().getETag();

    return factory.withTransaction(
      (session, tx) -> // load to memory a managed instance so that update may be done with preloaded instance with given locking type
        session.find(ProjectDbo.class, eid)
          
          // avoid starting update with stale version:
          .map(guardVersion(etag))
          .flatMap(this::guardVersion)

          // apply requested changes
          .invoke(it -> applyCommand(it, request))

          // flush to get back updated instance with new version field
          .call(session::flush)

          .map(this::getVersion)
          .map(it -> UpdateResult.newBuilder().setEntityId(it).build()));
  }

  /**
   * Currently Panache does not protect stale updates throwing OptimisticLockException, so we need to protect version manually
   * Should be reviewed when closed https://github.com/quarkusio/quarkus/issues/7193
   */
  private Function1<ProjectDbo, Either<Exception, ProjectDbo>> guardVersion(long expectedETag) {
    return dbo -> Objects.equals(dbo.getVersion(), expectedETag)
      ? Either.right(dbo)
      : Either.left(Status.FAILED_PRECONDITION.withDescription("Invalid version").asException());
  }

  private Uni<? extends ProjectDbo> guardVersion(Either<Exception, ProjectDbo> it) {
    return it.isRight()
      ? Uni.createFrom().item(it.get())
      : Uni.createFrom().failure(it.getLeft());
  }

  private ProjectId getVersion(ProjectDbo it) {
    return ProjectId.newBuilder()
      .setEId(it.getEntityId().toString())
      .setETag(it.getVersion())
      .build();
  }

  private void applyCommand(ProjectDbo dbEntity, UpdateCommand cmd) {
    dbEntity.setName(cmd.getModel().getName());
    dbEntity.setEmailOfOwner(cmd.getModel().getEmailOfOwner());
  }

}
