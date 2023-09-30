package sinnet.grpc.reports.report1;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import io.vavr.control.Try;
import lombok.experimental.UtilityClass;
import sinnet.models.ActionDuration;
import sinnet.models.Distance;

interface Models {

  /**
  * Represents printable data content of a report. Useful for testing before being converted to PDF file.
  */
  record ReportRequest(

      // Details about the customer whome the activities have been addressed.
      CustomerDetails customer,

      // List of the activities.
      List<ActivityDetails> activities) { }

  record CustomerDetails(
      String customerName,
      String customerCity,
      String address) { }

  record ReportRequests(List<ReportRequest> items) { }

  record ActivityDetails(
      String description,
      String who,
      Optional<LocalDate> when,
      ActionDuration howLongInMins,
      Distance howFarInKms) { }

  record SpecialActivityDetails(
      String description,
      String who,
      Optional<LocalDate> when,
      ActionDuration howLongInMins,
      Distance howFarInKms) { }

  @UtilityClass
  class Mapper {

    public static ReportRequest map(sinnet.report1.grpc.ReportRequest dto) {
      var customer = map(dto.getCustomer());
      var details = dto.getDetailsList().stream().map(Mapper::map).toList();
      return new ReportRequest(customer, details);
    }

    private static CustomerDetails map(sinnet.report1.grpc.CustomerDetails dto) {
      return new CustomerDetails(dto.getCustomerName(), dto.getCustomerCity(), dto.getCustomerAddress());
    }

    static ActivityDetails map(sinnet.report1.grpc.ActivityDetails dto) {
      return new ActivityDetails(
        dto.getDescription(),
        dto.getWho(),
        map(dto.getWhen()),
        ActionDuration.of(dto.getHowLongInMins()),
        Distance.of(dto.getHowFarInKms()));
    }

    private static Optional<LocalDate> map(sinnet.reports.grpc.Date dto) {
      return Try.ofCallable(() -> LocalDate.of(dto.getYear(), dto.getMonth(), dto.getDayOfTheMonth())).toJavaOptional();
    }
  }

}
