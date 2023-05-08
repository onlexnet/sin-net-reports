package sinnet.reports.report2;

import sinnet.reports.shared.Kilometers;
import sinnet.reports.shared.Minutes;

import java.time.LocalDate;

import io.vavr.collection.Iterator;
import io.vavr.collection.List;
import lombok.experimental.UtilityClass;

/** Converts ReportRequest (DTO) to its local model. */
@UtilityClass
class DtoDomainMapper {

  static ActivityDetails fromDto(sinnet.report2.grpc.ActivityDetails x) {
    return new ActivityDetails(x.getYearMonth(), x.getPersonName(), Kilometers.of(x.getHowFarInKms()), Minutes.of(x.getHowLongInMins()));
  }
    
  static ReportRequest apply(sinnet.report2.grpc.ReportRequest dto) {
    return new ReportRequest(List.ofAll(dto.getDetailsList()).map(it -> fromDto(it)));
  }
}
