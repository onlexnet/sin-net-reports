package sinnet.read;

import java.util.UUID;

import io.vertx.core.Future;
import sinnet.models.Email;

public interface RolesProjector {

  interface Provider {

    Future<Role> find(Email email, UUID projectId);
  }

  enum Role {
    NONE,
    USER,
  }

}
