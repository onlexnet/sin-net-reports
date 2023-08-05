package sinnet;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.BootstrapWith;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
// examples https://github.com/cucumber/cucumber-jvm/pull/1911
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@BootstrapWith(SpringBootDbTestContextBootstrapper.class)
@ActiveProfiles("test")
class CucumberSpringConfiguration {
}
