package sinnet;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;
import sinnet.db.SqlServerDbExtension;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(SqlServerDbExtension.class)
class ActuatorTests {

  @LocalServerPort
  private int port;
  
  @BeforeEach
  public void beforeEach() {
    RestAssured.port = port;
  }

  @Test
  public void shouldBeLive() {
    given()
        .when().get("/healtz")
        .then()
        .statusCode(200);
  }

  @Test
  public void shouldBeReady() {
    given()
        .when().get("/readyz")
        .then()
        .statusCode(200);
  }

}
