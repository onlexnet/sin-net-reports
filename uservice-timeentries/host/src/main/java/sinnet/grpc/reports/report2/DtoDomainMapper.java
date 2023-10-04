package sinnet.grpc.reports.report2;

import lombok.experimental.UtilityClass;
import sinnet.grpc.common.Mapper;
import sinnet.grpc.reports.report2.Models.ActivityDetails;
import sinnet.grpc.reports.report2.Models.ReportRequest;
import sinnet.models.ActionDuration;
import sinnet.models.Distance;

/** Converts ReportRequest (DTO) to its local model. */
@UtilityClass
class DtoDomainMapper {

  static ActivityDetails fromDto(sinnet.report2.grpc.ActivityDetails dto) {
    return new ActivityDetails(Mapper.fromDto(dto.getYearMonth()),
      dto.getPersonName(),
      Distance.of(dto.getHowFarInKms()),
      ActionDuration.of(dto.getHowLongInMins()));
  }
    
  static ReportRequest fromDto(sinnet.report2.grpc.ReportRequest dto) {
    return new ReportRequest(dto.getDetailsList().stream().map(it -> fromDto(it)).toList());
  }

}
