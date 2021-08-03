package sinnet;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.tracing.opentelemetry.OpenTelemetryOptions;

/** Single configuration class for Vertx related components. */
@TestConfiguration
public class VertxConfigurer {

  /**
   * Fixme.
   *
   * @return shared vertx instance use across layers
   */
  @Bean
  public Vertx vertx() {
    return Vertx.vertx(
      new VertxOptions()
      .setTracingOptions(
        new OpenTelemetryOptions())
    );
  }

  /** Fixme.
   *
   * @param vertx fixme
   * @return fixme
   */
  @Bean
  public EventBus vertxEventBus(Vertx vertx) {
    return vertx.eventBus();
  }
}
