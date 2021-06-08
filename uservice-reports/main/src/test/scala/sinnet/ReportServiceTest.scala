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
import sinnet.reports.ReportRequest
import sinnet.reports.ReportsGrpc
import org.assertj.core.api.Assertions

@QuarkusTest
class ReportsGrpcTest {
    
    @Inject
    @GrpcService("self")
    var client: ReportsGrpc.ReportsBlockingStub = _

    @Test
    def produceEndpoint() = {
        val request = ReportRequest.newBuilder().build
        val res = client.produce(request)
        Assertions.assertThat(res.getData().toByteArray()).isNotEmpty()
    }

}
