package sinnet;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

import onlexnet.sinnet.webapi.test.AppApi;

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
    var ar = new int[] {1, 2, 3, 4, 4, 1 ,1 };
    var aaa1 = IntStream.of(ar).collect(
      () -> new HashMap<Integer, Integer>(),
      (acc, i) -> { 
        acc.compute(i, (k, v) -> v == null ? i : v + i);  },
      (i1, i2) -> { });
    
  }
}
