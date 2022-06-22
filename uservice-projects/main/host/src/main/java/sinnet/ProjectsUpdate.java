package sinnet;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.mutiny.Mutiny.Session;

import io.grpc.Status;
import io.smallrye.mutiny.Uni;
import io.vavr.Function1;
import io.vavr.control.Either;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import sinnet.grpc.projects.ProjectId;
import sinnet.grpc.projects.UpdateCommand;
import sinnet.grpc.projects.UpdateResult;

public interface ProjectsUpdate {
  Uni<UpdateResult> update(UpdateCommand request);
}

@ApplicationScoped
@Slf4j
class ProjectsUpdateImpl implements ProjectsUpdate {

  @Inject
  ProjectRepository projectsRepo;

  @Inject
  Mutiny.SessionFactory factory;
  
  @Override
  public Uni<UpdateResult> update(UpdateCommand request) {
    var eidAsString = request.getEntityId().getEId();
    var eid = UUID.fromString(eidAsString);
    var etag = request.getEntityId().getETag();
    var emailOfOwner = request.getModel().getEmailOfOwner();
    var newEntityTemplate = new ProjectDbo()
        .setEntityId(eid)
        .setVersion(etag)
        .setName("-")
        .setEmailOfOwner("-");

    return factory.withTransaction(
      (session, tx) -> // load to memory a managed instance so that update may be done with preloaded instance with given locking type
        session.find(ProjectDbo.class, eid)
          // if no existing instance, lets continue on a new one
          .map(Optional::ofNullable)
          .map(it -> it.orElse(newEntityTemplate))
          
          // if it is new entity, lets check if creating it may increase number of free projects more that allowed
          // TODO: it is naive implementation as multiple threads in parallel may create projects above the limit
          .flatMap(guardLimits(3, newEntityTemplate, session, emailOfOwner))

          .call(session::persist)

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

  private <T> Function<? super T, Uni<? extends T>> guardLimits(long limit, T detachedEntity, Session session, String emailOfOwner) {
    return dbo -> {
      if (dbo != detachedEntity) {
        return Uni.createFrom().item(dbo);
      } else {
        val query = "SELECT count(*) from ProjectDbo T where T.emailOfOwner=:emailOfOwner";
        return session.createQuery(query, Long.class)
          .setParameter("emailOfOwner", emailOfOwner)
          .getSingleResult()
          .map(Long::intValue)
          .flatMap(count -> count >= 3
            ? Uni.createFrom().failure(Status.RESOURCE_EXHAUSTED.withDescription("Too many projects").asException())
            : Uni.createFrom().item(detachedEntity));
      }
    };
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
