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
import sinnet.reports.{ ReportRequest => ReportRequestDTO,
        ReportRequests => ReportRequestsDTO,
        CustomerDetails => CustomerDetailsDTO,
        ActivityDetails => ActivityDetailsDTO}
import sinnet.reports.ReportsGrpc
import org.assertj.core.api.Assertions
import java.util.zip.ZipInputStream
import java.io.ByteArrayInputStream

@QuarkusTest
class ReportServiceTest {
    
    @Inject
    @GrpcService("self")
    var client: ReportsGrpc.ReportsBlockingStub = _

    @Test
    def produceReportWithMinDataCase1(): Unit = {
        val customer = CustomerDetailsDTO.newBuilder().build()
        val activity = ActivityDetailsDTO.newBuilder().build();
        val request = ReportRequestDTO.newBuilder().build()
        val res = client.produce(request)
        var data = res.getData().toByteArray()
        Assertions.assertThat(data).isNotEmpty()
    }

    @Test
    def produceReportWithMinDataCase2(): Unit = {
        val customer = CustomerDetailsDTO.newBuilder().build()
        val request = ReportRequestDTO.newBuilder()
            .setCustomer(customer)
            .addDetails(ActivityDetailsDTO
                .newBuilder()
                .setDescription("Position 1")
                .setHowFarInKms(23)
                .build())
            .addDetails(ActivityDetailsDTO
                .newBuilder()
                .setDescription("Position 2")
                .setHowLongInMins(12)
                .setHowFarInKms(34)
                .build())
            .build()
        val res = client.produce(request)
        var data = res.getData().toByteArray()
        
        // uncomment block of lines below to produce a local example raport file
        // import java.io.File
        // import java.nio.file.Files
        // import java.nio.file.Paths
        // Files.write(Paths.get("temp_raport_from_test.pdf"), data)

        Assertions.assertThat(data).isNotEmpty()
    }

    @Test
    def producePackEndpoint(): Unit = {
        val request = ReportRequestDTO.newBuilder().build
        val pack = ReportRequestsDTO.newBuilder()
            .addItems(request)
            .addItems(request)
            .build()
        val res = client.producePack(pack)

        var data = res.getData().toByteArray()
        val byteStream = new ByteArrayInputStream(data)
        val zis = new ZipInputStream(byteStream)

        var entry1 = zis.getNextEntry()
        var entry2 = zis.getNextEntry()
        var entry3 = zis.getNextEntry()
        Assertions.assertThat(entry1.getSize()).isNotZero()
        Assertions.assertThat(entry2.getSize()).isNotZero()
        Assertions.assertThat(entry3).isNull()

    }
}
