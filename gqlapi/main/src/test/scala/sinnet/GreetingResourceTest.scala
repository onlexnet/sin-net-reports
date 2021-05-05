package sinnet

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test
import io.quarkus.grpc.runtime.annotations.GrpcService;
import io.quarkus.example.GreeterGrpc;
import io.quarkus.example.HelloRequest;
import javax.inject.Inject

@QuarkusTest
class GreetingResourceTest {

    @Inject
    @GrpcService("hello")
    var client: GreeterGrpc.GreeterBlockingStub;

    @Test
    def testHelloEndpoint() = {
        given()
          .`when`().get("/hello-resteasy")
          .then()
             .statusCode(200)
             .body(`is`("Hello RESTEasy"))
    }

}