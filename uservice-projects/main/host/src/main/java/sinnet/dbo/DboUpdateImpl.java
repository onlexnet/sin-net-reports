package sinnet.dbo;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import io.grpc.Status;
import io.vavr.Function1;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import sinnet.model.ProjectVid;
import sinnet.model.ValEmail;

@Component
@AllArgsConstructor
class DboUpdateImpl implements DboUpdate {

  private final ProjectRepository repository;
  
  @Override
  public ProjectVid updateCommand(ProjectVid vid, UpdateCommandContent content) {
    var eid = vid.id();
    var etag = vid.tag();

    // load to memory a managed instance so that update may be done with preloaded instance with given locking type
    var current = repository.findById(eid)
        .map(guardVersion(etag))
        .map(this::guardVersion)
        .get();

    // apply requested changes
    applyCommand(current, content);

    repository.flush();
    return getVersion(current);
  }

  private Function1<ProjectDbo, Either<Exception, ProjectDbo>> guardVersion(long expectedETag) {
    return dbo -> Objects.equals(dbo.getVersion(), expectedETag)
      ? Either.right(dbo)
      : Either.left(Status.FAILED_PRECONDITION.withDescription("Invalid version").asException());
  }

  @SneakyThrows
  private ProjectDbo guardVersion(Either<Exception, ProjectDbo> it) {
    if (it.isLeft()) {
      throw it.getLeft();
    }
    return it.get();
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
