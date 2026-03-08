package sinnet.web;

import java.io.IOException;

import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class CustomAuthenticationFilter extends OncePerRequestFilter {

  private static final String PRINCIPAL_ID_HEADER = "X-MS-CLIENT-PRINCIPAL-ID";
  private static final String PRINCIPAL_NAME_HEADER = "X-MS-CLIENT-PRINCIPAL-NAME";
  private static final String ACTUATOR_HEALTH_PATH_PREFIX = "/actuator/health";
  private static final String ERROR_PATH = "/error";

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    var requestUri = request.getRequestURI();
    var method = request.getMethod();

    // Health probes and technical endpoints should bypass authentication parsing.
    return HttpMethod.OPTIONS.matches(method)
        || ERROR_PATH.equals(requestUri)
        || requestUri.startsWith(ACTUATOR_HEALTH_PATH_PREFIX);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // https://learn.microsoft.com/en-us/azure/container-apps/authentication#access-user-claims-in-application-code
    var id = request.getHeader(PRINCIPAL_ID_HEADER);
    var username = request.getHeader(PRINCIPAL_NAME_HEADER);
    log.info("Authentication: ID:{}, Name:{}", id, username);

    if (username == null && id == null) {
      // not our responsibility. delegate down the chain. maybe a different filter
      // will understand this request.
      filterChain.doFilter(request, response);
      return;
    }

    if (username == null || id == null) {
      // user is clearly trying to authenticate against the
      // CustomAuthenticationFilter, but has done something wrong.
      response.setStatus(401);
      return;
    }

    // construct one of Spring's auth tokens
    var authentication = new AuthenticationToken(id, username);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response);

  }

}
