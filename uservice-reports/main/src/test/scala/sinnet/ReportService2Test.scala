package sinnet

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test
import io.quarkus.example.GreeterGrpc;
import io.quarkus.example.HelloRequest;
import javax.inject.Inject
import scala.annotation.meta.field
import sinnet.reports.{YearMonth => YearMonthDTO}
import sinnet.reports.report2.{
        ReportRequest => ReportRequestDTO,
        ActivityDetails => ActivityDetailsDTO}
import sinnet.reports.report2.ReportsGrpc
import org.assertj.core.api.Assertions
import java.util.zip.ZipInputStream
import java.io.ByteArrayInputStream
import io.quarkus.grpc.GrpcClient
import org.junit.jupiter.api.Timeout

@QuarkusTest
@Timeout(3)
class ReportService2Test {
    
    @Inject
    @GrpcClient
    var self: ReportsGrpc.ReportsBlockingStub = _

    @Test
    def produceReportWithMinDataCase1(): Unit = {
        val activity = ActivityDetailsDTO.newBuilder().build()
        val request = ReportRequestDTO.newBuilder().build()
        val res = self.produce(request)
        var data = res.getData().toByteArray()
        Assertions
            .assertThat(data)
            .isNotEmpty()
    }

    @Test
    def produceReportWithMinDataCase2(): Unit = {
        var period = YearMonthDTO.newBuilder().setYear(2001).setMonth(1);
        val request = ReportRequestDTO.newBuilder()
            .addDetails(ActivityDetailsDTO
                .newBuilder()
                .setPersonName("Ala")
                .setYearMonth(period)
                .build())
            .addDetails(ActivityDetailsDTO
                .newBuilder()
                .setPersonName("Ola")
                .setYearMonth(period)
                .build())
            .build()
        val res = self.produce(request)
        var data = res.getData().toByteArray()
        
        // uncomment block of lines below to produce a local example raport file
        import java.io.File
        import java.nio.file.Files
        import java.nio.file.Paths
        Files.write(Paths.get("temp_raport2_from_test.pdf"), data)

        Assertions.assertThat(data).isNotEmpty()
    }

    // @Test
    // def producePackEndpoint(): Unit = {
    //     val request = ReportRequestDTO.newBuilder().build
    //     val pack = ReportRequestsDTO.newBuilder()
    //         .addItems(request)
    //         .addItems(request)
    //         .build()
    //     val res = self.producePack(pack)

    //     var data = res.getData().toByteArray()
    //     val byteStream = new ByteArrayInputStream(data)
    //     val zis = new ZipInputStream(byteStream)

    //     var entry1 = zis.getNextEntry()
    //     var entry2 = zis.getNextEntry()
    //     var entry3 = zis.getNextEntry()
    //     Assertions.assertThat(entry1.getSize()).isNotZero()
    //     Assertions.assertThat(entry2.getSize()).isNotZero()
    //     Assertions.assertThat(entry3).isNull()

    // }
}
