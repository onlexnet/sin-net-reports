package sinnet.read;

import java.util.UUID;

import sinnet.models.ValEmail;

public interface RolesProjector {

  interface Provider {
    Role find(ValEmail email, UUID projectId);
  }

  enum Role {
    /** Either project is not defined, or user in the project is not defined. */
    NONE,
    
    USER,
  }

}
