package sinnet;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/** local configuration files. */
@Configuration
@PropertySources({
    @PropertySource("classpath:web-server-${spring.profiles.active:default}.properties"),
    @PropertySource("classpath:web-server.properties")
})
public class WebServerConfig {
}
