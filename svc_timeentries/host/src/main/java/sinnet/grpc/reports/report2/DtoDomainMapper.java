package sinnet.grpc.reports.report2;

import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.dbo.DboFacade;
import sinnet.domain.model.ValEmail;
import sinnet.domain.model.ValProjectId;
import sinnet.grpc.common.Mapper;
import sinnet.grpc.reports.report2.Models.ActivityDetails;
import sinnet.grpc.reports.report2.Models.ReportRequest;
import sinnet.models.ActionDuration;
import sinnet.models.Distance;

/** Converts ReportRequest (DTO) to its local model. */
@Component
@RequiredArgsConstructor
class DtoDomainMapper {

  private final DboFacade dboFacade;

  ActivityDetails fromDto(sinnet.report2.grpc.ActivityDetails dto) {
    return new ActivityDetails(Mapper.fromDto(dto.getYearMonth()),
      dto.getPersonName(),
      Distance.of(dto.getHowFarInKms()),
      ActionDuration.of(dto.getHowLongInMins()));
  }
    
  ReportRequest fromDto(sinnet.report2.grpc.ReportRequest dto) {
    var requestorEmail = dto.getUserToken().getRequestorEmail();
    var projectIdAsString = dto.getProjectId();
    var projectId = UUID.fromString(projectIdAsString);
    var isOwner = dboFacade.isOwner(ValEmail.of(requestorEmail), ValProjectId.of(projectId));

    Predicate<ActivityDetails> filter = isOwner
        ? item -> true
        : item -> item.personName().equalsIgnoreCase(requestorEmail);
    var activities = dto.getDetailsList().stream().map(it -> fromDto(it)).filter(filter).toList();

    return new ReportRequest(activities);
  }

}
