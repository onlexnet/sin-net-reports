package sinnet;

import java.util.UUID;

import io.vavr.collection.Stream;
import reactor.core.publisher.Mono;
import sinnet.models.Email;

/** Provides some projections where User is the central entity. */
public interface UsersProjector {

  Mono<Stream<UserModel>> search(UUID projectId, Email serviceMan);
}
