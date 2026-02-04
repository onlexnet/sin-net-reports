package sinnet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import sinnet.db.SqlServerDbExtension;

// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Program.class)
@SpringBootTest(classes = Program.class)
@ActiveProfiles("test")
@ExtendWith(SqlServerDbExtension.class)
class MyTest {

  @Autowired
  WebApplicationContext webContext;

  @Test
  public void shouldBeLive() {
    var restClient = RestTestClient.bindToApplicationContext(webContext).build();
  }

}
