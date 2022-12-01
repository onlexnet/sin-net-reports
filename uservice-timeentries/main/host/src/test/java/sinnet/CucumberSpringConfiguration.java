package sinnet;

import org.springframework.test.context.BootstrapWith;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@BootstrapWith(SpringBootDbTestContextBootstrapper.class)
class CucumberSpringConfiguration {
}
