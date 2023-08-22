package sinnet.web;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Designed to be used behind Azure Container Apps authentication.
 */
@Component
@RequestScope
@RequiredArgsConstructor
class AuthProviderImpl implements AuthProvider {

  private final HttpServletRequest request;
  private AuthenticationResult current;

  /** The bean is scoped to request, so we need to read headers only once as they should stay the same. */
  @PostConstruct
  void init() {
    // claims delivered by Container Apps:
    // https://learn.microsoft.com/en-us/azure/container-apps/authentication#access-user-claims-in-application-code
    final var principalNameHeaderName = "X-MS-CLIENT-PRINCIPAL-NAME";
    final var principalNameHeaderId = "X-MS-CLIENT-PRINCIPAL-ID";
    var principalName = request.getHeader(principalNameHeaderName);
    var principalId = request.getHeader(principalNameHeaderId);

    current = principalId == null || principalName == null
      ? Anonymous.INSTANCE
      : new Authenticated(principalId, principalName);
  }

  @Override
  public AuthenticationResult current() {
    return current;
  }
  
}
