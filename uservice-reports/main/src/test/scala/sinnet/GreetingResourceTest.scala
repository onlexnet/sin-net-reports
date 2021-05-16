package sinnet

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test
import io.quarkus.grpc.runtime.annotations.GrpcService;
import io.quarkus.example.GreeterGrpc;
import io.quarkus.example.HelloRequest;
import javax.inject.Inject
import scala.annotation.meta.field
import org.junit.jupiter.api.Assertions

@QuarkusTest
class GreetingResourceTest {
    
    @Inject
    @GrpcService("hello")
    var client: GreeterGrpc.GreeterBlockingStub = _

    @Test
    def testHelloEndpoint() = {
        var request = HelloRequest.newBuilder().setName("Ala ma kota").build()
        var a = client.sayHello(request)
        Assertions.assertEquals(a.getMessage(), "Hello from uservice Reposts, Ala ma kota")
    }

}
