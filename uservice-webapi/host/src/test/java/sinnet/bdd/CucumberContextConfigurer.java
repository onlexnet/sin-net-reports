package sinnet.bdd;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.cucumber.spring.CucumberContextConfiguration;
import sinnet.Profiles;
import sinnet.app.Program;

@CucumberContextConfiguration
@ActiveProfiles({Profiles.App.TEST})
@DirtiesContext
@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = { "DAPR_GRPC_PORT=0" }, classes = { Program.class, PortsConfigurer.class })
public class CucumberContextConfigurer {
}
