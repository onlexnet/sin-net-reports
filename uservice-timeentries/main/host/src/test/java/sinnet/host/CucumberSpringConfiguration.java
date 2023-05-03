package sinnet.host;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.BootstrapWith;

import io.cucumber.spring.CucumberContextConfiguration;
import sinnet.SpringBootDbTestContextBootstrapper;

@CucumberContextConfiguration
// examples https://github.com/cucumber/cucumber-jvm/pull/1911
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = HostContextConfiguration.class)
@BootstrapWith(SpringBootDbTestContextBootstrapper.class)
@ActiveProfiles("test")
class CucumberSpringConfiguration {
}
