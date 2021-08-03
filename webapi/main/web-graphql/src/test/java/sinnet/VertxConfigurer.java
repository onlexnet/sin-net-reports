package sinnet;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

/** Single configuration class for Vertx related components. */
@TestConfiguration
public class VertxConfigurer {

  @Bean
  public Vertx vertx() {
    return Vertx.vertx();
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
