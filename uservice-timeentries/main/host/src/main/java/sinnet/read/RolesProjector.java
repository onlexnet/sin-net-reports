package sinnet.read;

import java.util.UUID;

import sinnet.domain.model.ValEmail;

/**
 * TBD.
 */
public interface RolesProjector {

  /**
   * TBD.
   */
  interface Provider {
    Role find(ValEmail email, UUID projectId);
  }

  /**
   * TBD.
   */
  enum Role {
    /** Either project is not defined, or user in the project is not defined. */
    NONE,
    
    USER,
  }

}
