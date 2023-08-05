package sinnet;

import static io.restassured.RestAssured.given;

import java.net.URI;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import io.restassured.RestAssured;
import sinnet.db.SqlServerDbExtension;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { Program.class })
@ActiveProfiles("test")
@ExtendWith(SqlServerDbExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ActuatorTests {

  @Autowired
  private TestRestTemplate testRestTemplate;
  
  // @BeforeEach
  // public void beforeEach() {
  //   var uriAsString = testRestTemplate.getRootUri();
  //   var currentPort = URI.create(uriAsString).getPort();
  //   RestAssured.port = currentPort;
  // }

  @Test
  public void getHealth() {
    // given()

    //     .when().get("/actuator/health")
    //     .then()
    //     .statusCode(200)
    //     .body("components.livenessState.status", Matchers.equalTo("UP"));
  }

}
