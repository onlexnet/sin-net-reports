package sinnet;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest
@ExtendWith(SpringExtension.class)
// examples https://github.com/cucumber/cucumber-jvm/pull/1911
@BootstrapWith(SpringBootDbTestContextBootstrapper.class)
class CucumberSpringConfiguration {
}
