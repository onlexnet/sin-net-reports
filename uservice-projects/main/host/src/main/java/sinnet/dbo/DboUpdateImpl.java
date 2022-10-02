package sinnet.dbo;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;

import org.hibernate.reactive.mutiny.Mutiny;

import io.grpc.Status;
import io.smallrye.mutiny.Uni;
import io.vavr.Function1;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import sinnet.model.ProjectVid;
import sinnet.model.ValEmail;

@ApplicationScoped
@RequiredArgsConstructor
class DboUpdateImpl implements DboUpdate {

  private final Mutiny.SessionFactory factory;
  
  @Override
  public Uni<ProjectVid> updateCommand(ProjectVid vid, UpdateCommandContent content) {
    var eid = vid.id();
    var etag = vid.tag();

    return factory.withTransaction(
      (session, tx) -> // load to memory a managed instance so that update may be done with preloaded instance with given locking type
        session.find(ProjectDbo.class, eid)
          
          // avoid starting update with stale version:
          .map(guardVersion(etag))
          .flatMap(this::guardVersion)

          // apply requested changes
          .invoke(it -> applyCommand(it, content))

          // flush to get back updated instance with new version field
          .call(session::flush)

          .map(this::getVersion));
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

  private ProjectVid getVersion(ProjectDbo it) {
    return ProjectVid.of(it.getEntityId(), it.getVersion());
  }

  private void applyCommand(ProjectDbo dbEntity, UpdateCommandContent cmd) {
    var operators = Stream.of(cmd.operators()).map(ValEmail::value).collect(Collectors.toSet());
    dbEntity.setName(cmd.name());
    dbEntity.setEmailOfOwner(cmd.newOwner().value());
    dbEntity.setOperators(operators);
  }

}
