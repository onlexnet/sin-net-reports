package sinnet.web;

/**
 * Allows to get currently logged user based on active http request.
 * If no active request is ongoing, ???
 */
public interface AuthProvider {

  AuthenticationResult current();

  /** Fixme. */
  sealed interface AuthenticationResult {}

  /** Fixme. */
  record Authenticated(String id, String name) implements AuthenticationResult {
  }

  /** Fixme. */
  enum Anonymous implements AuthenticationResult {
    INSTANCE
  }

}
