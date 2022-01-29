package sinnet;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/** local configuration files. */
@Configuration
@PropertySources({
    @PropertySource("classpath:grpc-server.properties"),
    @PropertySource(
        value = "classpath:grpc-server-${spring.profiles.active:default}.properties",
        ignoreResourceNotFound = true)
})
public class GrpcServerProperties {
}
