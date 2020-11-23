package sinnet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

/** Single configuration class for Vertx related components. */
@Configuration
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
