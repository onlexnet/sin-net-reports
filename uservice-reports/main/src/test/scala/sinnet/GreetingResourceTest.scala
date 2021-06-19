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
import sinnet.reports.{ReportRequest => ReportRequestDTO}
import org.assertj.core.api.Assertions

@QuarkusTest
class GreetingResourceTest {
    
    @Inject
    @GrpcService("self")
    var client: GreeterGrpc.GreeterBlockingStub = _

    @Test
    def testHelloEndpoint(): Unit = {
        var request = HelloRequest.newBuilder().setName("Ala ma kota").build()
        var a = client.sayHello(request)
        Assertions.assertThat(a.getMessage()).startsWith("Hello from uservice Reposts, Ala")
    }
}
