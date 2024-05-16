package sinnet.otp;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import sinnet.infra.InfraConfigurer;

/** Registers types in the current module. */
@Configuration
@ComponentScan
@Import(InfraConfigurer.class)
public class OtpConfigurer {
}
