package sinnet.web;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class CustomAuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // https://learn.microsoft.com/en-us/azure/container-apps/authentication#access-user-claims-in-application-code
    final var principalNameHeaderId = "X-MS-CLIENT-PRINCIPAL-ID";
    final var principalNameHeaderName = "X-MS-CLIENT-PRINCIPAL-NAME";

    var username = request.getHeader(principalNameHeaderName);
    var id = request.getHeader(principalNameHeaderId);

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
