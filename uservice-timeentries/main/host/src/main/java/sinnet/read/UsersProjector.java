package sinnet.read;

import java.util.UUID;

import io.vavr.collection.Stream;
import sinnet.models.Email;

/** Provides some projections where User is the central entity. */
public interface UsersProjector {

  Stream<UserModel> search(UUID projectId, Email serviceMan);
}
