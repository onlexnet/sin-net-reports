package sinnet.read;

import java.util.UUID;

import sinnet.models.Email;

public interface RolesProjector {

  interface Provider {
    Role find(Email email, UUID projectId);
  }

  enum Role {
    /** Either project is not defined, or user in the project is not defined. */
    NONE,
    
    USER,
  }

}
