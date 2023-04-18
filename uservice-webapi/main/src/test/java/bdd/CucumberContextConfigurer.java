package bdd;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import io.cucumber.spring.CucumberContextConfiguration;
import sinnet.Program;

@CucumberContextConfiguration
@ContextConfiguration(classes = { Program.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"DAPR_GRPC_PORT=0"})
public class CucumberContextConfigurer {
}
