package sinnet;

import java.util.UUID;

import io.vavr.collection.Stream;
import io.vertx.core.Future;
import sinnet.models.Email;

/** Provides some projections where User is the central entity. */
public interface UsersProjector {

  Future<Stream<UserModel>> search(UUID projectId, Email serviceMan);
}
