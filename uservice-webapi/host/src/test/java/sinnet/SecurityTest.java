package sinnet;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.UUID;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

import onlexnet.sinnet.webapi.test.AppApi;
import sinnet.app.Program;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = Program.class)
@ActiveProfiles(Profiles.App.TEST)
class GqlTest {

  @Autowired
  TestRestTemplate restTemplate;

  AppApi appApi;

  @Before
  public void before() {
    var requestorEmail = "email@" + UUID.randomUUID();
    appApi = new AppApi(restTemplate.getRootUri(), requestorEmail);
  }

  @Test
  void doSomething() {
  }
}
