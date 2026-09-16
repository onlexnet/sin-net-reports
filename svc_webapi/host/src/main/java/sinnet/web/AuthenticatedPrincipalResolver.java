package sinnet.web;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Resolves the current principal into a stable internal shape for app flows. */
@Component
public class AuthenticatedPrincipalResolver {

  /** Resolve principal from the active security context. */
  public AuthenticatedPrincipal currentPrincipal() {
    return fromAuthentication(SecurityContextHolder.getContext().getAuthentication());
  }

  /** Resolve principal from either legacy header auth or JWT-backed authentication. */
  public AuthenticatedPrincipal fromAuthentication(@Nullable Authentication authentication) {
    if (authentication == null) {
      return new AuthenticatedPrincipal("anonymous", "anonymous");
    }

    if (authentication instanceof AuthenticationToken legacyToken) {
      return new AuthenticatedPrincipal(
          legacyToken.getId(),
          legacyToken.getPrincipal());
    }

    var id = firstNonBlank(
        claimAsString(authentication, "sub"),
        claimAsString(authentication, "oid"),
        claimAsString(authentication, "id"),
        nullToBlank(authentication.getName()))
        .orElse("unknown");

    var email = firstNonBlank(
        claimAsString(authentication, "email"),
        claimAsString(authentication, "preferred_username"),
        claimAsString(authentication, "upn"),
        nullToBlank(authentication.getName()))
        .orElse("unknown");

    return new AuthenticatedPrincipal(id, email);
  }

  private static Optional<String> claimAsString(Authentication authentication, String claimName) {
    return claimValue(authentication, claimName)
        .map(Object::toString)
        .map(String::trim)
        .filter(it -> !it.isBlank());
  }

  private static Optional<Object> claimValue(Authentication authentication, String claimName) {
    return claimValue(authentication.getPrincipal(), claimName)
        .or(() -> claimValue(authentication.getDetails(), claimName));
  }

  private static Optional<Object> claimValue(@Nullable Object source, String claimName) {
    if (source == null) {
      return Optional.empty();
    }

    if (source instanceof Map<?, ?> map) {
      return Optional.ofNullable(map.get(claimName));
    }

    if (source instanceof Principal principal) {
      if ("name".equals(claimName) && principal.getName() != null) {
        return Optional.of(principal.getName());
      }
    }

    return invokeClaimMethod(source, "getClaimAsString", claimName)
        .or(() -> invokeClaimMethod(source, "getClaim", claimName))
        .or(() -> invokeClaimsMap(source, claimName));
  }

  private static Optional<Object> invokeClaimsMap(Object source, String claimName) {
    try {
      var method = source.getClass().getMethod("getClaims");
      var result = method.invoke(source);
      if (result instanceof Map<?, ?> claims) {
        return Optional.ofNullable(claims.get(claimName));
      }
      return Optional.empty();
    } catch (ReflectiveOperationException ignored) {
      return Optional.empty();
    }
  }

  private static Optional<Object> invokeClaimMethod(Object source, String methodName, String claimName) {
    try {
      var method = source.getClass().getMethod(methodName, String.class);
      return Optional.ofNullable(method.invoke(source, claimName));
    } catch (ReflectiveOperationException ignored) {
      return Optional.empty();
    }
  }

  private static Optional<String> firstNonBlank(Optional<String>... values) {
    for (var value : values) {
      if (value.isPresent()) {
        return value;
      }
    }
    return Optional.empty();
  }

  private static Optional<String> nullToBlank(@Nullable String value) {
    return Optional.ofNullable(value)
        .map(String::trim)
        .filter(it -> !it.isBlank());
  }
}