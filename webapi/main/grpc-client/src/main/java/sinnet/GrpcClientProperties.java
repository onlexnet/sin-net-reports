package sinnet;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/** local configuration files. */
@Configuration
@PropertySources({
    @PropertySource("classpath:grpc-client.properties"),
    @PropertySource(
        value = "classpath:grpc-client-${spring.profiles.active:default}.properties",
        ignoreResourceNotFound = true)
})
public class GrpcClientProperties {
}
