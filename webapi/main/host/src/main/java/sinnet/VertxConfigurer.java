package sinnet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.tracing.opentelemetry.OpenTelemetryOptions;

/** Single configuration class for Vertx related components. */
@Configuration
public class VertxConfigurer {

  /**
   * Shared vertx instance to be used across the app instance.
   *
   * @return Vertx singleton instance
   */
  @Bean
  public Vertx vertx() {
    var options = new OpenTelemetryOptions();
    return Vertx.vertx(
      new VertxOptions()
      .setTracingOptions(options));
  }

  /** 
   * EventBus property of shared Verx instance.
   * Equivalent of @Autowired Vertx vertx; vertx.eventBus().
   *
   * @param vertx Shared Vertx instance
   * @return vertx.eventBus() shared instance
   */
  @Bean
  public EventBus vertxEventBus(Vertx vertx) {
    return vertx.eventBus();
  }
}
