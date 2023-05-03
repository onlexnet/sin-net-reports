package sinnet.host;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

import groovy.util.logging.Slf4j;
import io.cucumber.spring.CucumberContextConfiguration;
import sinnet.HostTestContextBootstrapper;

@CucumberContextConfiguration
// examples https://github.com/cucumber/cucumber-jvm/pull/1911
@BootstrapWith(HostTestContextBootstrapper.class)
@ContextConfiguration(classes = HostTestContextConfiguration.class)
@ActiveProfiles("test")
@Slf4j
class CucumberSpringConfiguration {
}
