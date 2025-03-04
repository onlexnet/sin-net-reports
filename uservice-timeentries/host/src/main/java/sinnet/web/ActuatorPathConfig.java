package sinnet.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Exposes Actuator health endpoints using well-known addresses, expected by default ny k8s.
 */
@Configuration
class ActuatorPathConfig implements WebMvcConfigurer {
  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addRedirectViewController("/healtz", "/actuator/health/liveness");
    registry.addRedirectViewController("/readyz", "/actuator/health/readiness");
  }
}
