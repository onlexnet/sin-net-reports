package sinnet.reports;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import sinnet.AppOperations;
import sinnet.Profiles;
import sinnet.db.PostgresDbExtension;
import sinnet.host.HostTestContextConfiguration;
import sinnet.report1.grpc.ActivityDetails;
import sinnet.report1.grpc.CustomerDetails;
import sinnet.report1.grpc.ReportRequest;
import sinnet.report1.grpc.ReportsGrpc;

// import io.quarkus.example.GreeterGrpc
// import io.quarkus.example.HelloRequest
// import io.quarkus.grpc.GrpcClient
// import io.quarkus.test.junit.QuarkusTest
// import io.restassured.RestAssured.given
// import org.assertj.core.api.Assertions
// import org.hamcrest.CoreMatchers.`is`
// import org.junit.jupiter.api.Nested
// import org.junit.jupiter.api.Test
// import sinnet.report1.grpc.ReportsGrpc

// import java.io.ByteArrayInputStream
// import java.util.zip.ZipInputStream
// import javax.inject.Inject
// import scala.annotation.meta.field

// import collection.JavaConverters._
// import sinnet.reports.grpc.Date

@SpringBootTest
@ContextConfiguration(classes = { HostTestContextConfiguration.class })
@ActiveProfiles(Profiles.TEST)
@ExtendWith(PostgresDbExtension.class)
class ReportService1Test {
    
    @Autowired
    AppOperations self;

    @Test
    void produceReportWithMinDataCase1() {
        var customer = CustomerDetails.newBuilder().build();
        var activity = ActivityDetails.newBuilder().build();
        var request = ReportRequest.newBuilder().build();
        var res = self.produce(request);
        var data = res.getData().toByteArray();

        Assertions.assertThat(data).isNotEmpty();
    }

//     @Test
//     def produceReportWithMinDataCase2(): Unit = {
//         val customer = CustomerDetailsDTO.newBuilder()
//             .setCustomerName("Customer name")
//             .setCustomerId("Customer ID")
//             .setCustomerAddress("Customer Address")
//             .setCustomerCity("Customer City")
//             .build()
//         val who = "PERSON WITH LONG NAME 1 LINE";
//         val when = Date.newBuilder().setYear(2001).setMonth(2).setDayOfTheMonth(3).build()
//         val request = ReportRequestDTO.newBuilder()
//             .setCustomer(customer)
//             .addDetails(ActivityDetailsDTO
//                 .newBuilder()
//                 .setWho(who)
//                 .setWhen(when)
//                 .setDescription("Position 1")
//                 .setHowFarInKms(23)
//                 .build())
//             .addDetails(ActivityDetailsDTO
//                 .newBuilder()
//                 .setWho(who)
//                 .setWhen(when)
//                 .setDescription("Position 2")
//                 .setHowLongInMins(12)
//                 .setHowFarInKms(34)
//                 .build())
//             .build()
//         val res = self.produce(request)
//         var data = res.getData().toByteArray()
        
//         // uncomment block of lines below to produce a local example raport file
//         import java.io.File
//         import java.nio.file.Files
//         import java.nio.file.Paths

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;
//         Files.write(Paths.get("temp_raport1_from_test.pdf"), data)

//         Assertions.assertThat(data).isNotEmpty()
//     }

//     @Test
//     def producePackEndpoint(): Unit = {
//         val request = ReportRequestDTO.newBuilder().build
//         val pack = ReportRequestsDTO.newBuilder()
//             .addItems(request)
//             .addItems(request)
//             .build()
//         val res = self.producePack(pack)

//         var data = res.getData().toByteArray()
//         val byteStream = new ByteArrayInputStream(data)
//         val zis = new ZipInputStream(byteStream)

//         var entry1 = zis.getNextEntry()
//         var entry2 = zis.getNextEntry()
//         var entry3 = zis.getNextEntry()
//         Assertions.assertThat(entry1.getSize()).isNotZero()
//         Assertions.assertThat(entry2.getSize()).isNotZero()
//         Assertions.assertThat(entry3).isNull()
//     }

//     /**
//       * https://github.com/onlexnet/sin-net-reports/issues/73
//       * Three digits for element number
//       */
//     @Test
//     def files_should_be_numerated_using_three_sigits(): Unit = {
//         val request = ReportRequestDTO.newBuilder().build
//         val pack = Stream.from(1).take(3)
//                 .foldLeft(ReportRequestsDTO.newBuilder())((acc, v) => {
//                     val item = ReportRequestDTO.newBuilder()
//                     acc.addItems(item)
//                 })
//                 .build()
//         val res = self.producePack(pack)

//         var data = res.getData().toByteArray()
//         val byteStream = new ByteArrayInputStream(data)
//         val zis = new ZipInputStream(byteStream)

//         val fileNames = Iterator.iterate((zis.getNextEntry(), zis)){case (entry, zis) => (zis.getNextEntry(), zis)}
//             .takeWhile{case (entry, _) => entry != null}
//             .map( _._1)
//             .map(_.getName())
//             .toArray
//         Assertions.assertThat(fileNames).containsExactly("001-.pdf", "002-.pdf", "003-.pdf")
//     }

//     /**
//       * https://github.com/onlexnet/sin-net-reports/issues/73
//       * Slash in client name produces valid filename
//       */
//     @Test
//     def files_should_be_normalized_when_customer_has_special_characters_in_name(): Unit = {
//         var companyWithSlashInName = ReportRequestDTO
//             .newBuilder()
//             .setCustomer(CustomerDetailsDTO.newBuilder().setCustomerName("My/Company"))
//             .build()
//         val request = ReportRequestsDTO.newBuilder()
//             .addItems(companyWithSlashInName)
//             .build()
//         val res = self.producePack(request)

//         var data = res.getData().toByteArray()
//         val byteStream = new ByteArrayInputStream(data)
//         val zis = new ZipInputStream(byteStream)

//         var entry1 = zis.getNextEntry()
//         Assertions.assertThat(entry1.getName()).isEqualTo("001-My_Company.pdf")
//     }

//     // /** https://github.com/onlexnet/sin-net-reports/issues/60 */
//     // @Test
//     // class 
}
