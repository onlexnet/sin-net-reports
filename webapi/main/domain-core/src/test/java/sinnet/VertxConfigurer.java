package sinnet;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

/** Vertx bean is required by the app, so we have to create its simple equivalent for tests. */
@TestConfiguration
public class VertxConfigurer {

  /**
   * Fixme.
   * @return fixme
   */
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
