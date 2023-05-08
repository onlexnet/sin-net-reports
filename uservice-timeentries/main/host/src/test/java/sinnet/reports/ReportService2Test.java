package sinnet.reports;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import sinnet.AppOperations;
import sinnet.db.PostgresDbExtension;
import sinnet.host.HostTestContextConfiguration;
import sinnet.report2.grpc.ActivityDetails;
import sinnet.report2.grpc.ReportRequest;
import sinnet.Profiles;

@SpringBootTest
@ContextConfiguration(classes = { HostTestContextConfiguration.class })
@ActiveProfiles(Profiles.TEST)
@ExtendWith(PostgresDbExtension.class)
@Timeout(value = 3)
class ReportService2Test {

  @Autowired
  AppOperations self;

  @Test
  void produceReportWithMinDataCase1() {
    var activity = ActivityDetails.newBuilder().build();
    var request = ReportRequest.newBuilder().build();
    var service = self.getSelfReport2();
    var res = service.produce(request);
    var data = res.getData().toByteArray();
    Assertions
        .assertThat(data)
        .isNotEmpty();
  }

  // @Test
  // def produceReportWithMinDataCase2(): Unit = {
  // var period = YearMonthDTO.newBuilder().setYear(2001).setMonth(1);
  // val request = ReportRequestDTO.newBuilder()
  // .addDetails(ActivityDetailsDTO
  // .newBuilder()
  // .setPersonName("Ala")
  // .setYearMonth(period)
  // .setHowLongInMins(42)
  // .build())
  // .addDetails(ActivityDetailsDTO
  // .newBuilder()
  // .setPersonName("Ola")
  // .setYearMonth(period)
  // .setHowLongInMins(123)
  // .setHowFarInKms(4)
  // .build())
  // .build()
  // val res = self.produce(request)
  // var data = res.getData().toByteArray()

  // // uncomment block of lines below to produce a local example raport file
  // import java.io.File
  // import java.nio.file.Files
  // import java.nio.file.Paths
  // import java.util.concurrent.TimeUnit;

  // import org.junit.jupiter.api.Timeout;
  // import org.junit.jupiter.api.Timeout.ThreadMode;
  // import org.springframework.beans.factory.annotation.Autowired;

  // import sinnet.AppOperations;
  // import sinnet.report2.grpc.ActivityDetails;
  // import sinnet.report2.grpc.ReportRequest;
  // Files.write(Paths.get("temp_raport2_from_test.pdf"), data)

  // Assertions.assertThat(data).isNotEmpty()
  // }

}
