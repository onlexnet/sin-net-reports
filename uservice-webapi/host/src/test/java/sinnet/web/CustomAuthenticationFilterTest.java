package sinnet.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

class CustomAuthenticationFilterTest {

  private final CustomAuthenticationFilter filter = new CustomAuthenticationFilter();

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldBypassActuatorHealthForReadiness() throws Exception {
    var request = new MockHttpServletRequest("GET", "/actuator/health/readiness");
    var response = new MockHttpServletResponse();
    var chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(chain.getRequest()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  void shouldBypassActuatorHealthForLiveness() throws Exception {
    var request = new MockHttpServletRequest("GET", "/actuator/health/liveness");
    var response = new MockHttpServletResponse();
    var chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(chain.getRequest()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  void shouldBypassOptionsRequests() throws Exception {
    var request = new MockHttpServletRequest("OPTIONS", "/graphql");
    var response = new MockHttpServletResponse();
    var chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(chain.getRequest()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  void shouldBypassErrorEndpoint() throws Exception {
    var request = new MockHttpServletRequest("GET", "/error");
    var response = new MockHttpServletResponse();
    var chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(chain.getRequest()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  void shouldContinueWithoutAuthenticationWhenHeadersMissing() throws Exception {
    var request = new MockHttpServletRequest("GET", "/graphql");
    var response = new MockHttpServletResponse();
    var chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(chain.getRequest()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  void shouldReturnUnauthorizedWhenOnlyOneHeaderExists() throws Exception {
    var request = new MockHttpServletRequest("GET", "/graphql");
    request.addHeader("X-MS-CLIENT-PRINCIPAL-ID", "account-id");
    var response = new MockHttpServletResponse();
    var chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertThat(response.getStatus()).isEqualTo(401);
    assertThat(chain.getRequest()).isNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  void shouldPopulateAuthenticationWhenBothHeadersExist() throws Exception {
    var request = new MockHttpServletRequest("GET", "/graphql");
    request.addHeader("X-MS-CLIENT-PRINCIPAL-ID", "account-id");
    request.addHeader("X-MS-CLIENT-PRINCIPAL-NAME", "user@example.com");
    var response = new MockHttpServletResponse();
    var chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(chain.getRequest()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isInstanceOf(AuthenticationToken.class);

    var authentication = (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication.getId()).isEqualTo("account-id");
    assertThat(authentication.getName()).isEqualTo("user@example.com");
  }
}
