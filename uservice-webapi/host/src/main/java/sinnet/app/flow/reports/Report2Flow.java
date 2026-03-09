package sinnet.app.flow.reports;

import static sinnet.reports.grpc.YearMonth.newBuilder;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import lombok.RequiredArgsConstructor;
import sinnet.domain.models.UserToken;
import sinnet.app.ports.in.Report2PortIn;
import sinnet.app.ports.out.ActionsGrpcPortOut;
import sinnet.report2.grpc.ReportRequest;
import sinnet.report2.grpc.ReportsGrpc.ReportsBlockingStub;
import sinnet.web.AuthenticationToken;

@Component
@RequiredArgsConstructor
class Report2Flow implements Report2PortIn {

  private final ActionsGrpcPortOut timeentries;
  private final ReportsBlockingStub reportsClient;

  @Override
  public byte[] downloadPdfFile(UUID projectId, YearMonth from, YearMonth to) {

    var authentication = (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    var primaryEmail = authentication.getPrincipal();

    var dateFrom = from.atDay(1);
    var dateTo = to.atDay(1).plusMonths(1).minusDays(1);

    var entries = getTimeentries(projectId, dateFrom, dateTo);
    var reportRequest = asReportRequest(entries, primaryEmail, projectId);
    var data = reportsClient.produce(reportRequest);
    return data.toByteArray();
  }

  // TODO move such aggregation logic close to data service
  ReportRequest asReportRequest(List<TimeEntryModel> javaItems, String primaryEmail, UUID projectId) {
    var items = io.vavr.collection.List.ofAll(javaItems);
    var map = items
        .map(it -> Tuple.of(it.servicemanName(), YearMonth.from(it.when()), it.howLong(), it.howFar()))
        .map(it -> {
          var key = Tuple.of(it._1, it._2);
          return Tuple.of(key, Tuple.of(it._3, it._4));
        })
        .foldLeft(
          HashMap.<Tuple2<String, YearMonth>, Tuple2<Integer, Integer>>empty(),
          (acc, v) -> acc.put(v._1, v._2, (v1, v2) -> Tuple.of(v1._1 + v2._1, v1._2 + v2._2)));

    var requestBuilder = ReportRequest.newBuilder();
    map.forEach((k, v) -> requestBuilder.addDetailsBuilder()
        .setPersonName(k._1)
        .setYearMonth(newBuilder().setYear(k._2.getYear()).setMonth(k._2.getMonthValue()))
        .setHowLongInMins(v._1)
        .setHowFarInKms(v._2)
        .build());
    var userToken = new UserToken(projectId, primaryEmail);
    requestBuilder.setUserToken(sinnet.reports.grpc.UserToken.newBuilder()
      .setRequestorEmail(userToken.requestorEmail())
      .build());
    requestBuilder.setProjectId(userToken.projectId().toString());
    return requestBuilder.build();
  }

  record TimeEntryModel(String customerId,
                        String what,
                        LocalDate when,
                        String servicemanName,
                        int howFar,
                        int howLong) {
  }

  private List<TimeEntryModel> getTimeentries(UUID projectId, LocalDate from, LocalDate to) {
    var data = timeentries.searchInternal(projectId, from, to).stream()
        .map(it -> new TimeEntryModel(it.customerId(),
        it.description(),
        it.whenProvided(),
        it.servicemanName(),
        it.distance(),
        it.duration()))
        .toList();
    return data;

  }

}
