package sinnet.web;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * Test utility for setting up authentication in MockMvc tests.
 */
public final class TestAuthenticationHelper {

  private TestAuthenticationHelper() {
    // Utility class
  }

  /**
   * Creates a RequestPostProcessor that sets up authentication headers for CustomAuthenticationFilter.
   * Sets X-MS-CLIENT-PRINCIPAL-ID and X-MS-CLIENT-PRINCIPAL-NAME headers.
   */
  public static RequestPostProcessor withUser(String email) {
    return request -> {
      request.addHeader("X-MS-CLIENT-PRINCIPAL-ID", "test-user-id");
      request.addHeader("X-MS-CLIENT-PRINCIPAL-NAME", email);
      return request;
    };
  }

  /**
   * Creates an AuthenticationToken for testing.
   */
  public static AuthenticationToken createTestToken(String email) {
    return new AuthenticationToken("test-user-id", email);
  }
}
