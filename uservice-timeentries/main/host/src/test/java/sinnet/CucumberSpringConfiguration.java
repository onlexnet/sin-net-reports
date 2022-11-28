package sinnet;

import org.springframework.test.context.ContextConfiguration;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@ContextConfiguration(classes = DiConfigurer.class)
class CucumberSpringConfiguration {
}
