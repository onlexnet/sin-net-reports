package sinnet.access;

import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

import io.grpc.Status;
import io.vavr.Function1;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import sinnet.grpc.projects.UserToken;
import sinnet.model.ValProjectId;

/** Entry to check if the user has access to requested action. */
@Component
@RequiredArgsConstructor
class UserAccess implements AccessFacade {

  private final AccessAs<?>[] accessAs;

  @Override
  @SneakyThrows
  public void guardAccess(UserToken requestor, ValProjectId eid,
      Function<RoleContext, Predicate<ValProjectId>> methodExtractor) {
    var exists = Option.of(this.from(requestor))
        .map(methodExtractor::apply)
        .map(this::guardRole)
        .map(it -> it.apply(eid))
        .map(this::eitherMap)
        .isDefined();
      if (!exists) {
        throw Status.FAILED_PRECONDITION.withDescription("Illegal owner").asException();
      }
  }

  /**
   * Converts user token to list of all contexts where the user have access to.
   */
  private RoleContext from(UserToken userToken) {
    var sets = Stream
        .of(accessAs)
        .map(it -> (RoleContextSet) it.calculate(userToken));
    return new RoleContext(sets);
  }

  private Function1<ValProjectId, Either<Exception, ValProjectId>> guardRole(Predicate<ValProjectId> canContinue) {
    return id -> canContinue.test(id)
        ? Either.right(id)
        : Either.left(Status.FAILED_PRECONDITION.withDescription("Illegal owner").asException());
  }

  @SneakyThrows
  private ValProjectId eitherMap(Either<Exception, ValProjectId> it) {
    if (it.isLeft()) {
      throw it.getLeft();
    }

    return it.get();
  }

}
