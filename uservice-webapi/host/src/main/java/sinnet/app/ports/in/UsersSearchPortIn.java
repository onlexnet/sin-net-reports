package sinnet.app.ports.in;

import java.util.UUID;

/** DocMe. */
public interface UsersSearchPortIn {
  
  /** DoxMe. */
  public record User(String email, String entityId) { }

  /** DoxMe. */
  Iterable<User> search(UUID projectId, String requestor);
}
