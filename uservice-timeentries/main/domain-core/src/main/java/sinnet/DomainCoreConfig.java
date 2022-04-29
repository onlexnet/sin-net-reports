package sinnet;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/** local configuration files. */
@Configuration
@PropertySources({
    @PropertySource("classpath:domain-core.properties"),
    @PropertySource(
        value = "classpath:domain-core-${spring.profiles.active:default}.properties",
        ignoreResourceNotFound = true)
})
public class DomainCoreConfig {
}
