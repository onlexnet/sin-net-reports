package sinnet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.opentelemetry.api.OpenTelemetry;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.metrics.MetricsOptions;
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
    var tracingOptions = new OpenTelemetryOptions();
    var metricOptions = new MetricsOptions()
        .setEnabled(true);
    return Vertx.vertx(
      new VertxOptions()
      .setTracingOptions(tracingOptions)
      .setMetricsOptions(metricOptions));
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
