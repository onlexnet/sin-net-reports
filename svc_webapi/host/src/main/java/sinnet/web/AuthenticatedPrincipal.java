package sinnet.web;

/** Normalized authenticated principal used across legacy and JWT auth modes. */
public record AuthenticatedPrincipal(String id, String email) {
}
