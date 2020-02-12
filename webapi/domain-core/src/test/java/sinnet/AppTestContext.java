package sinnet;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/** Class "loads" Axon framework and domain logic. */
@Configuration
@ComponentScan(basePackageClasses = {sinnet.PackageMarker.class})
@EnableAutoConfiguration
public class AppTestContext {
}
