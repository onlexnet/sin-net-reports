package sinnet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.tracing.zipkin.ZipkinTracingOptions;

/** Single configuration class for Vertx related components. */
@Configuration
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
        new ZipkinTracingOptions().setServiceName("A cute service")));
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
