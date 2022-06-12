package sinnet;

import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.hibernate.reactive.mutiny.Mutiny;

import com.google.common.base.Objects;

import io.grpc.Status;
import io.smallrye.mutiny.Uni;
import io.vavr.Function1;
import io.vavr.control.Either;
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
    var eIdAsString = request.getEntityId().getEId();
    var eId = UUID.fromString(eIdAsString);
    var eTag = request.getEntityId().getETag();
    var newEntityTemplate = new ProjectDbo()
        .setEntityId(eId)
        .setVersion(eTag)
        .setName("-")
        .setEmailOfOwner("-")
        .setTemp(true);

    return factory.withTransaction(
      (session, tx) -> // load to memory a managed instance so that merge may be done with preloaded instance with given locking type
        session.find(ProjectDbo.class, eId)
          // if no existing instance, lets continue on a new one
          .map(Optional::ofNullable)
          .map(it -> it.orElse(newEntityTemplate))
          .call(session::persist)

          // verify if the version, requested by client, is still valid
          .map(guardVersion(eTag))
          .flatMap(this::guardVersion)

          // apply requested changes
          .invoke(it -> applyCommand(it, eTag, request))

          // flush to get stored updated instance with new version field
          .call(session::flush)

          .map(this::getVersion)
          .map(it -> UpdateResult.newBuilder().setEntityId(it).build()));




    //  projectsRepo.findById(eId, LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    //   .map(Optional::ofNullable)
    //   .map(it -> it.map(this.guardVersion(eTag)).orElse(Either.right(newEntityTemplate)))
    //   .flatMap(this::guardVersion)
    //   .invoke(it -> logInfo(it, "SIUDEKK after (optional) load {}"))
    //   .invoke(it -> applyCommand(it, request))
    //   .flatMap(projectsRepo::persistAndFlush)
    //   .invoke(it -> logInfo(it, "SIUDEKK after persist {}"))
    //   .map(this::incrementedVersion)
    //   .map(it -> UpdateResult.newBuilder().setEntityId(it).build());
  }

  /**
   * Currently Panache does not protect stale updates throwing OptimisticLockException, so we need to protect version manually
   * Should be reviewed when closed https://github.com/quarkusio/quarkus/issues/7193
   */
  private Function1<ProjectDbo, Either<Exception, ProjectDbo>> guardVersion(long expectedETag) {
    return dbo -> Objects.equal(dbo.getVersion(), expectedETag)
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

  private void applyCommand(ProjectDbo dbEntity, long expectedVersion, UpdateCommand cmd) {
    dbEntity.setName(cmd.getName());
    dbEntity.setEmailOfOwner(cmd.getEmailOfOwner());
  }

}
