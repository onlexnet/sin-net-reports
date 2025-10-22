package sinnet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import sinnet.app.Program;

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = { "DAPR_GRPC_PORT=0" }, classes = { Program.class})

@ActiveProfiles(profiles = Profiles.App.DEV)
class ApplicationContextTest {

  @Test
  void initializeContext() {
  }

}
